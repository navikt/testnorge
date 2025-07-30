package no.nav.dolly.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.tpsmessagingservice.MiljoerConsumer;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingKontroll;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Dokument;
import no.nav.dolly.domain.jpa.Dokument.DokumentType;
import no.nav.dolly.domain.resultset.BestilteKriterier;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingLeggTilPaaGruppe;
import no.nav.dolly.domain.resultset.RsDollyImportFraPdlRequest;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.dokarkiv.RsDokarkiv;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingFragment;
import no.nav.dolly.domain.resultset.histark.RsHistark;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.DokumentRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.reactivesecurity.action.GetAuthenticatedUserId;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static no.nav.dolly.util.DistinctByKeyUtil.distinctByKey;
import static org.apache.commons.lang3.StringUtils.isNoneBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@RequiredArgsConstructor
public class BestillingService {

    private static final String NOT_FOUND = "Finner ikke gruppe basert på gruppeId: ";
    private static final String SEARCH_STRING = "info:";
    private static final String DEFAULT_VALUE = null;

    private final BestillingElasticRepository elasticRepository;
    private final BestillingKontrollRepository bestillingKontrollRepository;
    private final BestillingProgressRepository bestillingProgressRepository;
    private final BestillingRepository bestillingRepository;
    private final BrukerService brukerService;
    private final DokumentRepository dokumentRepository;
    private final GetAuthenticatedUserId getAuthenticatedUserId;
    private final IdentRepository identRepository;
    private final MalBestillingService malBestillingService;
    private final MiljoerConsumer miljoerConsumer;
    private final ObjectMapper objectMapper;
    private final TestgruppeRepository testgruppeRepository;

    public Mono<Bestilling> fetchBestillingById(Long bestillingId) {

        return bestillingRepository.findById(bestillingId)
                .flatMap(bestilling -> getBestillingProgresser(bestilling)
                        .map(progresser -> {
                            bestilling.setProgresser(progresser);
                            return bestilling;
                        }))
                .switchIfEmpty(Mono.error(new NotFoundException(format("Fant ikke bestillingId %d", bestillingId))));
    }

    public Flux<RsBestillingFragment> fetchBestillingByFragment(String bestillingFragment) {

        var searchQueries = bestillingFragment.split(" ");
        var bestillingID = Arrays.stream(searchQueries)
                .filter(word -> word.matches("\\d+"))
                .findFirst()
                .orElse("");
        var gruppeNavn = Arrays.stream(searchQueries)
                .filter(word -> !word.equals(bestillingID))
                .collect(Collectors.joining(" "));

        if (isNoneBlank(gruppeNavn) && isNoneBlank(bestillingID)) {
            return bestillingRepository.findByIdContainingAndGruppeNavnContaining(bestillingID, gruppeNavn);

        } else {
            return Flux.merge(
                            bestillingRepository.findByIdContaining(wrapSearchString(bestillingID)),
                            bestillingRepository.findByGruppenavnContaining(wrapSearchString(gruppeNavn)))
                    .filter(distinctByKey(RsBestillingFragment::getid));
        }
    }

    public Flux<Bestilling> fetchBestillingerByGruppeIdOgIkkeFerdig(Long gruppeId) {

        return bestillingRepository.findBestillingByGruppeId(gruppeId)
                .filter(bestilling -> !bestilling.isFerdig())
                .flatMap(bestilling -> getBestillingProgresser(bestilling)
                        .map(progresser -> {
                            bestilling.setProgresser(progresser);
                            return bestilling;
                        }));
    }

    public Mono<Set<String>> fetchBestilteMiljoerByGruppeId(Long gruppeId) {

        return bestillingRepository.findBestillingByGruppeId(gruppeId)
                .map(Bestilling::getMiljoer)
                .filter(StringUtils::isNotBlank)
                .flatMap(miljoer -> Flux.just(miljoer.split(",")))
                .collect(toSet());
    }

    public Mono<Integer> getPaginertBestillingIndex(Long bestillingId, Long gruppeId) {

        return bestillingRepository.getPaginertBestillingIndex(bestillingId, gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException("Bestilling med id %s ble ikke funnet i database".formatted(bestillingId))));
    }

    public Flux<Bestilling> getBestillingerFromGruppeIdPaginert(Long gruppeId, Integer pageNo, Integer pageSize) {

        return bestillingRepository.getBestillingerFromGruppeId(gruppeId, PageRequest.of(pageNo, pageSize))
                .flatMap(bestilling -> Mono.zip(Mono.just(bestilling),
                        testgruppeRepository.findById(bestilling.getGruppeId()),
                        getBestillingProgresser(bestilling)))
                .map(tuple -> {
                    tuple.getT1().setGruppeId(tuple.getT2().getId());
                    tuple.getT1().setProgresser(tuple.getT3());
                    return tuple.getT1();
                });
    }

    @Transactional
    @Retryable
    public Mono<Bestilling> cancelBestilling(Long bestillingId) {

        return bestillingKontrollRepository.findByBestillingId(bestillingId)
                .switchIfEmpty(bestillingKontrollRepository.save(BestillingKontroll.builder()
                        .bestillingId(bestillingId)
                        .stoppet(true)
                        .build()))
                .flatMap(bestKontroll -> fetchBestillingById(bestillingId))
                .zipWith(fetchBruker())
                .map(tuple2 -> {
                    tuple2.getT1().setStoppet(true);
                    tuple2.getT1().setFerdig(true);
                    tuple2.getT1().setSistOppdatert(now());
                    tuple2.getT1().setBruker(tuple2.getT2());
                    return tuple2.getT1();
                })
                .flatMap(bestillingRepository::save)
                .flatMap(bestilling -> getBestillingProgresser(bestilling)
                        .map(progresser -> {
                            bestilling.setProgresser(progresser);
                            return bestilling;
                        }));
    }

    public Mono<Boolean> isStoppet(Long bestillingId) {

        return bestillingKontrollRepository.findByBestillingId(bestillingId)
                .switchIfEmpty(Mono.just(BestillingKontroll.builder().stoppet(false).build()))
                .map(BestillingKontroll::isStoppet);
    }

    public Consumer<Bestilling> cleanBestilling() {

        return bestilling ->
                bestilling.getProgresser()
                        .forEach(progress -> Arrays.stream(progress.getClass().getMethods())
                                .filter(method -> method.getName().contains("get"))
                                .forEach(metode -> {
                                    try {
                                        var verdi = metode.invoke(progress, (Object[]) null);
                                        if (verdi instanceof String verdiString &&
                                                isNotBlank(verdiString) && verdiString.toLowerCase().contains(SEARCH_STRING)) {
                                            var oppdaterMetode = progress.getClass()
                                                    .getMethod("set" + metode.getName().substring(3), String.class);
                                            oppdaterMetode.invoke(progress, DEFAULT_VALUE);
                                        }
                                    } catch (NoSuchMethodException |
                                             IllegalAccessException |
                                             InvocationTargetException e) {
                                        log.error("Oppdatering av bestilling {} feilet ved stopp-kommando {}",
                                                bestilling.getId(), e.getMessage(), e);
                                    }
                                }));
    }

    @Transactional
    public Mono<Bestilling> saveBestilling(RsDollyUpdateRequest request, String ident) {

        if (nonNull(request.getPdldata())) {
            request.getPdldata().setOpprettNyPerson(null);
        }

        return identRepository.findByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException(format("Testident %s ble ikke funnet", ident))))
                .doOnNext(tuple -> fixAaregAbstractClassProblem(request.getAareg()))
                .flatMap(testgruppe -> Mono.zip(
                        Mono.just(testgruppe),
                        fetchBruker(),
                        getBestKriterier(request)))
                .flatMap(tuple ->
                        Mono.just(Bestilling.builder()
                                        .gruppeId(tuple.getT1().getGruppeId())
                                        .ident(ident)
                                        .antallIdenter(1)
                                        .navSyntetiskIdent(request.getNavSyntetiskIdent())
                                        .sistOppdatert(now())
                                        .miljoer(filterAvailable(request.getEnvironments()))
                                        .bruker(tuple.getT2())
                                        .brukerId(tuple.getT2().getId())
                                        .bestKriterier(tuple.getT3())
                                        .build())
                                .flatMap(bestillingRepository::save))
                .doOnNext(bestilling -> request.setId(bestilling.getId()))
                .flatMap(bestilling -> {
                    if (isNotBlank(request.getMalBestillingNavn())) {
                        return malBestillingService.saveBestillingMal(bestilling,
                                        request.getMalBestillingNavn(), bestilling.getBruker().getBrukerId())
                                .thenReturn(bestilling);
                    } else {
                        return Mono.just(bestilling);
                    }
                })
                .flatMap(bestilling -> getBestillingProgresser(bestilling)
                        .map(progresser -> {
                            bestilling.setProgresser(progresser);
                            return bestilling;
                        }));
    }

    @Transactional
    public Mono<Bestilling> saveBestilling(Long gruppeId, RsDollyBestilling request, Integer antall,
                                           List<String> opprettFraIdenter, Boolean navSyntetiskIdent, String beskrivelse) {

        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND + gruppeId)))
                .doOnNext(testgruppe -> fixAaregAbstractClassProblem(request.getAareg()))
                .flatMap(testgruppe -> Mono.zip(
                        Mono.just(testgruppe),
                        fetchBruker(),
                        getBestKriterier(request)))
                .flatMap(tuple ->
                        Mono.just(Bestilling.builder()
                                        .gruppeId(tuple.getT1().getId())
                                        .antallIdenter(antall)
                                        .navSyntetiskIdent(navSyntetiskIdent)
                                        .sistOppdatert(now())
                                        .miljoer(filterAvailable(request.getEnvironments()))
                                        .opprettFraIdenter(nonNull(opprettFraIdenter) ? join(",", opprettFraIdenter) : null)
                                        .bruker(tuple.getT2())
                                        .brukerId(tuple.getT2().getId())
                                        .beskrivelse(beskrivelse)
                                        .bestKriterier(tuple.getT3())
                                        .build())
                                .flatMap(bestillingRepository::save))
                .doOnNext(bestilling -> request.setId(bestilling.getId()))
                .flatMap(bestilling -> {
                    if (isNotBlank(request.getMalBestillingNavn())) {
                        return malBestillingService.saveBestillingMal(bestilling,
                                        request.getMalBestillingNavn(), bestilling.getBruker().getBrukerId())
                                .thenReturn(bestilling);
                    } else {
                        return Mono.just(bestilling);
                    }
                })
                .flatMap(bestilling -> getBestillingProgresser(bestilling)
                        .map(progresser -> {
                            bestilling.setProgresser(progresser);
                            return bestilling;
                        }));
    }

    @Transactional
    // Egen transaksjon på denne da bestillingId hentes opp igjen fra database i samme kallet
    public Mono<Bestilling> createBestillingForGjenopprettFraBestilling(Long bestillingId, String miljoer) {

        return fetchBestillingById(bestillingId)
                .zipWith(fetchBruker())
                .flatMap(tuple -> {
                    if (!tuple.getT1().isFerdig()) {
                        return Mono.error(new DollyFunctionalException(format("Du kan ikke starte gjenopprett før bestilling %d er ferdigstilt.", bestillingId)));
                    }
                    if (tuple.getT1().getProgresser().stream()
                            .noneMatch(progress -> isNotBlank(progress.getIdent()))) {
                        return Mono.error(new NotFoundException(format("Ingen testidenter funnet på bestilling: %d", bestillingId)));
                    }
                    return Mono.just(Bestilling.builder()
                            .gruppeId(tuple.getT1().getGruppeId())
                            .antallIdenter(tuple.getT1().getAntallIdenter())
                            .opprettFraIdenter(tuple.getT1().getOpprettFraIdenter())
                            .sistOppdatert(now())
                            .miljoer(filterAvailable(isNotBlank(miljoer) ? miljoer : tuple.getT1().getMiljoer()))
                            .opprettetFraId(bestillingId)
                            .bestKriterier("{}")
                            .bruker(tuple.getT2())
                            .brukerId(tuple.getT2().getId())
                            .build());
                })
                .flatMap(bestillingRepository::save)
                .flatMap(bestilling -> getBestillingProgresser(bestilling)
                        .map(progresser -> {
                            bestilling.setProgresser(progresser);
                            return bestilling;
                        }));
    }

    @Transactional
    // Egen transaksjon på denne da bestillingId hentes opp igjen fra database i samme kallet
    public Mono<Bestilling> createBestillingForGjenopprettFraIdent(String ident, List<String> miljoer) {

        return identRepository.findByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException(format("Testperson med ident %s ble ikke funnet.", ident))))
                .zipWith(fetchBruker())
                .map(tuple -> Bestilling.builder()
                        .gruppeId(tuple.getT1().getGruppeId())
                        .ident(ident)
                        .antallIdenter(1)
                        .bestKriterier("{}")
                        .sistOppdatert(now())
                        .miljoer(filterAvailable(miljoer))
                        .gjenopprettetFraIdent(ident)
                        .bruker(tuple.getT2())
                        .brukerId(tuple.getT2().getId())
                        .build())
                .flatMap(bestillingRepository::save)
                .flatMap(bestilling -> getBestillingProgresser(bestilling)
                        .map(progresser -> {
                            bestilling.setProgresser(progresser);
                            return bestilling;
                        }));
    }

    @Transactional
    // Egen transaksjon på denne da bestillingId hentes opp igjen fra database i samme kallet
    public Mono<Bestilling> createBestillingForGjenopprettFraGruppe(Long gruppeId, String miljoer) {

        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND + gruppeId)))
                .flatMap(testgruppe -> Mono.zip(
                        fetchBruker(),
                        identRepository.findByGruppeId(gruppeId, Pageable.unpaged())
                                .collectList()))
                .flatMap(tuple -> {
                    if (tuple.getT2().isEmpty()) {
                        return Mono.error(new NotFoundException(format("Ingen testpersoner funnet i gruppe: %d", gruppeId)));
                    } else {
                        return Mono.just(tuple);
                    }
                })
                .map(tuple -> Bestilling.builder()
                        .gruppeId(gruppeId)
                        .antallIdenter(tuple.getT2().size())
                        .bestKriterier("{}")
                        .sistOppdatert(now())
                        .miljoer(filterAvailable(miljoer))
                        .opprettetFraGruppeId(gruppeId)
                        .bruker(tuple.getT1())
                        .brukerId(tuple.getT1().getId())
                        .build())
                .flatMap(bestillingRepository::save)
                .flatMap(bestilling -> getBestillingProgresser(bestilling)
                        .map(progresser -> {
                            bestilling.setProgresser(progresser);
                            return bestilling;
                        }));
    }

    @Transactional
    public Mono<Bestilling> saveBestilling(Long gruppeId, RsDollyImportFraPdlRequest request) {

        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND + gruppeId)))
                .doOnNext(testgruppe -> fixAaregAbstractClassProblem(request.getAareg()))
                .flatMap(testgruppe -> Mono.zip(fetchBruker(),
                        getBestKriterier(request)))
                .map(tuple -> Bestilling.builder()
                        .gruppeId(gruppeId)
                        .kildeMiljoe("PDL")
                        .miljoer(filterAvailable(request.getEnvironments()))
                        .sistOppdatert(now())
                        .bruker(tuple.getT1())
                        .brukerId(tuple.getT1().getId())
                        .antallIdenter(request.getIdenter().size())
                        .pdlImport(join(",", request.getIdenter()))
                        .beskrivelse(request.getBeskrivelse())
                        .bestKriterier(tuple.getT2())
                        .build())
                .flatMap(bestillingRepository::save)
                .doOnNext(bestilling -> request.setId(bestilling.getId()))
                .flatMap(bestilling -> getBestillingProgresser(bestilling)
                        .map(progresser -> {
                            bestilling.setProgresser(progresser);
                            return bestilling;
                        }));
    }

    @Transactional
    public Mono<Bestilling> saveBestilling(Long gruppeId, RsDollyBestillingLeggTilPaaGruppe request) {

        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException(NOT_FOUND + gruppeId)))
                .doOnNext(testgruppe -> fixAaregAbstractClassProblem(request.getAareg()))
                .flatMap(testgruppe -> Mono.zip(
                        fetchBruker(),
                        identRepository.countByGruppeId(gruppeId),
                        getBestKriterier(request)))
                .doOnNext(tuple -> log.info("Antall testidenter {} i gruppe {} ", tuple.getT2(), gruppeId))
                .map(tuple -> Bestilling.builder()
                        .gruppeId(gruppeId)
                        .miljoer(filterAvailable(request.getEnvironments()))
                        .sistOppdatert(now())
                        .bruker(tuple.getT1())
                        .antallIdenter(tuple.getT2())
                        .navSyntetiskIdent(request.getNavSyntetiskIdent())
                        .bestKriterier(tuple.getT3())
                        .build())
                .flatMap(bestillingRepository::save)
                .doOnNext(bestilling -> request.setId(bestilling.getId()))
                .flatMap(bestilling -> getBestillingProgresser(bestilling)
                        .map(progresser -> {
                            bestilling.setProgresser(progresser);
                            return bestilling;
                        }));
    }

    public Mono<Void> slettBestillingerByGruppeId(Long gruppeId) {

        return bestillingRepository.findBestillingByGruppeId(gruppeId)
                .map(Bestilling::getId)
                .doOnNext(elasticRepository::deleteById)
                .collectList()
                .then(bestillingKontrollRepository.deleteByGruppeId(gruppeId))
                .then(bestillingProgressRepository.deleteByGruppeId(gruppeId))
                .then(bestillingRepository.deleteByGruppeId(gruppeId))
                .then();
    }

    public Mono<Void> slettBestillingByBestillingId(Long bestillingId) {

        return Mono.zip(bestillingProgressRepository.deleteByBestillingId(bestillingId),
                        bestillingKontrollRepository.deleteByBestillingWithNoChildren(bestillingId))
                .flatMap(tuple -> {
                    elasticRepository.deleteById(bestillingId);
                    return bestillingRepository.deleteById(bestillingId);
                });
    }

    public Mono<Void> slettBestillingByTestIdent(String ident) {

        return bestillingProgressRepository.findByIdent(ident)
                .map(BestillingProgress::getBestillingId)
                .flatMap(bestillingId -> bestillingProgressRepository.deleteByIdent(ident)
                        .then(bestillingKontrollRepository.deleteByBestillingWithNoChildren(bestillingId))
                        .then(bestillingRepository.deleteBestillingWithNoChildren(bestillingId))
                        .then(Mono.fromRunnable(() -> elasticRepository.deleteById(bestillingId))))
                .collectList()
                .then();
    }

    public Mono<Bestilling> swapIdent(String oldIdent, String newIdent) {

        return bestillingRepository.swapIdent(oldIdent, newIdent);
    }

    public Mono<String> getBestKriterier(RsDollyBestilling request) {

        return oppdaterDokarkivDokumenter(request.getDokarkiv(), request.getId())
                .then(oppdaterHistarkDokumenter(request.getHistark(), request.getId()))
                .then(Mono.just(
                        Objects.requireNonNull(toJson(BestilteKriterier.builder()
                                .aareg(request.getAareg())
                                .arbeidsplassenCV(request.getArbeidsplassenCV())
                                .arbeidssoekerregisteret(request.getArbeidssoekerregisteret())
                                .arenaforvalter(request.getArenaforvalter())
                                .bankkonto(request.getBankkonto())
                                .brregstub(request.getBrregstub())
                                .dokarkiv(request.getDokarkiv())
                                .etterlatteYtelser(request.getEtterlatteYtelser())
                                .fullmakt(request.getFullmakt())
                                .histark(request.getHistark())
                                .inntektsmelding(request.getInntektsmelding())
                                .inntektstub(request.getInntektstub())
                                .instdata(request.getInstdata())
                                .krrstub(request.getKrrstub())
                                .medl(request.getMedl())
                                .pdldata(request.getPdldata())
                                .pensjonforvalter(request.getPensjonforvalter())
                                .sigrunstub(request.getSigrunstub())
                                .sigrunstubPensjonsgivende(request.getSigrunstubPensjonsgivende())
                                .sigrunstubSummertSkattegrunnlag(request.getSigrunstubSummertSkattegrunnlag())
                                .skattekort(request.getSkattekort())
                                .skjerming(request.getSkjerming())
                                .sykemelding(request.getSykemelding())
                                .tpsMessaging(request.getTpsMessaging())
                                .udistub(request.getUdistub())
                                .yrkesskader(request.getYrkesskader())
                                .build()))));
    }

    private Mono<Void> oppdaterDokarkivDokumenter(List<RsDokarkiv> dokarkiv, Long bestillingId) {

        return Flux.fromIterable(dokarkiv)
                .flatMap(tema -> Flux.fromIterable(tema.getDokumenter())
                        .flatMap(dokument -> Flux.fromIterable(dokument.getDokumentvarianter())
                                .flatMap(dokumentVariant -> {
                                    if (isNotBlank(dokumentVariant.getFysiskDokument())) {
                                        return lagreDokument(dokumentVariant.getFysiskDokument(), bestillingId, DokumentType.BESTILLING_DOKARKIV)
                                                .doOnNext(dokumentVariant::setDokumentReferanse)
                                                .doOnNext(id -> dokumentVariant.setFysiskDokument(null));
                                    }
                                    return Mono.empty();
                                })))
                .collectList()
                .then();
    }

    private Mono<Void> oppdaterHistarkDokumenter(RsHistark histark, Long bestillingId) {

        if (nonNull(histark)) {
            return Flux.fromIterable(histark.getDokumenter())
                    .flatMap(dokument -> {
                        if (isNotBlank(dokument.getFysiskDokument())) {
                            return lagreDokument(dokument.getFysiskDokument(), bestillingId, DokumentType.BESTILLING_HISTARK)
                                    .doOnNext(dokument::setDokumentReferanse)
                                    .doOnNext(id -> dokument.setFysiskDokument(null));
                        }
                        return Mono.empty();
                    })
                    .collectList()
                    .then();
        }
        return Mono.empty();
    }

    private Mono<Long> lagreDokument(String dokument, Long bestillingId, DokumentType dokumentType) {

        return Mono.just(Dokument.builder()
                        .contents(dokument)
                        .bestillingId(bestillingId)
                        .dokumentType(dokumentType)
                        .build())
                .flatMap(dokumentRepository::save)
                .map(Dokument::getId);
    }

    private String filterAvailable(Collection<String> environments) {

        if (isNull(environments) || environments.isEmpty()) {
            return null;
        }

        var miljoer = miljoerConsumer.getMiljoer().block();

        if (isNull(miljoer)) {
            return null;
        }
        return environments.stream()
                .filter(miljoer::contains)
                .collect(Collectors.joining(","));
    }

    private String filterAvailable(String miljoer) {

        return isNotBlank(miljoer) ? filterAvailable(Arrays.asList(miljoer.split(","))) : null;
    }

    private String wrapSearchString(String searchString) {
        return isNotBlank(searchString) ? "%%%s%%".formatted(searchString) : "";
    }

    private Mono<Bruker> fetchBruker() {

        return getAuthenticatedUserId.call()
                .flatMap(brukerService::fetchBruker);
    }

    private String toJson(Object object) {
        try {
            if (nonNull(object)) {
                return objectMapper.writer().writeValueAsString(object);
            }
        } catch (JsonProcessingException | RuntimeException e) {
            log.info("Konvertering til Json feilet", e);
        }
        return null;
    }

    private static void fixAaregAbstractClassProblem(List<RsAareg> aaregdata) {

        aaregdata.forEach(arbeidforhold -> {
            if (nonNull(arbeidforhold.getArbeidsgiver())) {
                arbeidforhold.getArbeidsgiver().setAktoertype(
                        arbeidforhold.getArbeidsgiver() instanceof RsOrganisasjon ? "ORG" : "PERS");
            }
        });
    }

    private Mono<List<BestillingProgress>> getBestillingProgresser(Bestilling bestilling) {

        return bestillingProgressRepository.findByBestillingId(bestilling.getId())
                .collectList();
    }
}