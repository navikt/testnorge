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
import no.nav.dolly.domain.projection.RsBestillingFragment;
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
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.LocalDateTime.now;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

@Slf4j
@Service
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@RequiredArgsConstructor
public class BestillingService {

    private static final String FINNES_IKKE = "Finner ikke gruppe med id %d";
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
                .map(id -> "%" + id + "%")
                .findFirst()
                .orElse("");
        var gruppeNavn = Arrays.stream(searchQueries)
                .filter(word -> !word.matches("\\d+"))
                .filter(StringUtils::isNotBlank)
                .map(word -> "%" + word + "%")
                .collect(Collectors.joining(" "));

        return Mono.just(bestillingFragment)
                .flatMapMany(fragment -> isNotBlank(gruppeNavn) && isNotBlank(bestillingID) ?
                        bestillingRepository.findByIdContainingAndGruppeNavnContaining(bestillingID, gruppeNavn) :
                        Flux.merge(
                                bestillingRepository.findByIdContaining(wrapSearchString(bestillingID)),
                                bestillingRepository.findByGruppenavnContaining(wrapSearchString(gruppeNavn))))
                .sort(Comparator.comparing(RsBestillingFragment::getId));
    }

    public Flux<Bestilling> fetchBestillingerByGruppeIdOgIkkeFerdig(Long gruppeId) {

        return bestillingRepository.findByGruppeId(gruppeId)
                .filter(bestilling -> !bestilling.isFerdig())
                .flatMap(bestilling -> getBestillingProgresser(bestilling)
                        .map(progresser -> {
                            bestilling.setProgresser(progresser);
                            return bestilling;
                        }));
    }

    public Mono<Set<String>> fetchBestilteMiljoerByGruppeId(Long gruppeId) {

        return bestillingRepository.findByGruppeId(gruppeId)
                .doOnNext(bestilling -> log.info("Bestilling {}", bestilling))
                .filter(bestilling -> isNotBlank(bestilling.getMiljoer()))
                .map(Bestilling::getMiljoer)
                .flatMap(miljoer -> Flux.just(miljoer.split(",")))
                .collect(toSet());
    }

    public Mono<Integer> getPaginertBestillingIndex(Long bestillingId, Long gruppeId) {

        return bestillingRepository.getPaginertBestillingIndex(bestillingId, gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException("Bestilling med id %d ble ikke funnet i database".formatted(bestillingId))));
    }

    public Flux<Bestilling> getBestillingerFromGruppeIdPaginert(Long gruppeId, Integer pageNo, Integer pageSize) {

        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE.formatted(gruppeId))))
                .flatMapMany(gruppe ->
                        bestillingRepository.getBestillingerFromGruppeId(gruppeId, PageRequest.of(pageNo, pageSize))
                                .flatMap(bestilling -> Mono.zip(
                                        Mono.just(bestilling),
                                        Mono.just(gruppe),
                                        getBestillingProgresser(bestilling)))
                                .map(tuple -> {
                                    tuple.getT1().setGruppeId(tuple.getT2().getId());
                                    tuple.getT1().setProgresser(tuple.getT3());
                                    return tuple.getT1();
                                }));
    }

    @Transactional
    @Retryable
    public Mono<Bestilling> cancelBestilling(Long bestillingId) {

        return bestillingRepository.findById(bestillingId)
                .switchIfEmpty(Mono.error(new NotFoundException("Bestilling med id %d ikke funnet".formatted(bestillingId))))
                .flatMap(bestilling -> bestillingKontrollRepository.findByBestillingId(bestillingId))
                .switchIfEmpty(bestillingKontrollRepository.save(BestillingKontroll.builder()
                        .stoppet(true)
                        .bestillingId(bestillingId)
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

    public Mono<Void> cleanBestilling(Bestilling bestilling) {

        return bestillingProgressRepository.findByBestillingId(bestilling.getId())
                .doOnNext(progress -> Arrays.stream(progress.getClass().getMethods())
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
                        }))
                .flatMap(bestillingProgressRepository::save)
                .collectList()
                .then();
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
                        miljoerConsumer.getMiljoer()))
                .flatMap(tuple ->
                        Mono.just(Bestilling.builder()
                                        .gruppeId(tuple.getT1().getGruppeId())
                                        .ident(ident)
                                        .antallIdenter(1)
                                        .navSyntetiskIdent(request.getNavSyntetiskIdent())
                                        .sistOppdatert(now())
                                        .miljoer(filterAvailable(request.getEnvironments(), tuple.getT3()))
                                        .bruker(tuple.getT2())
                                        .brukerId(tuple.getT2().getId())
                                        .build())
                                .flatMap(bestillingRepository::save))
                .doOnNext(bestilling -> request.setId(bestilling.getId()))
                .flatMap(bestilling ->
                        getBestKriterier(request)
                                .flatMap(kriterier -> {
                                    bestilling.setBestKriterier(kriterier);
                                    return bestillingRepository.save(bestilling);
                                }))
                .flatMap(bestilling -> {
                    if (isNotBlank(request.getMalBestillingNavn())) {
                        return malBestillingService.saveBestillingMal(bestilling,
                                        request.getMalBestillingNavn(), bestilling.getBruker().getId())
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
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE + gruppeId)))
                .doOnNext(testgruppe -> fixAaregAbstractClassProblem(request.getAareg()))
                .flatMap(testgruppe -> Mono.zip(
                        Mono.just(testgruppe),
                        fetchBruker(),
                        miljoerConsumer.getMiljoer()))
                .flatMap(tuple ->
                        Mono.just(Bestilling.builder()
                                        .gruppeId(tuple.getT1().getId())
                                        .antallIdenter(antall)
                                        .navSyntetiskIdent(navSyntetiskIdent)
                                        .sistOppdatert(now())
                                        .miljoer(filterAvailable(request.getEnvironments(), tuple.getT3()))
                                        .opprettFraIdenter(nonNull(opprettFraIdenter) ? join(",", opprettFraIdenter) : null)
                                        .bruker(tuple.getT2())
                                        .brukerId(tuple.getT2().getId())
                                        .beskrivelse(beskrivelse)
                                        .build())
                                .flatMap(bestillingRepository::save))
                .doOnNext(bestilling -> request.setId(bestilling.getId()))
                .flatMap(bestilling ->
                        getBestKriterier(request)
                                .flatMap(kriterier -> {
                                    bestilling.setBestKriterier(kriterier);
                                    return bestillingRepository.save(bestilling);
                                }))
                .flatMap(bestilling -> {
                    if (isNotBlank(request.getMalBestillingNavn())) {
                        return malBestillingService.saveBestillingMal(bestilling,
                                        request.getMalBestillingNavn(), bestilling.getBruker().getId())
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
                .flatMap(bestilling -> Mono.zip(
                        Mono.just(bestilling),
                        fetchBruker(),
                        miljoerConsumer.getMiljoer()))
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
                            .miljoer(filterAvailable(isNotBlank(miljoer) ? miljoer : tuple.getT1().getMiljoer(), tuple.getT3()))
                            .opprettetFraId(bestillingId)
                            .bestKriterier("{}")
                            .bruker(tuple.getT2())
                            .brukerId(tuple.getT2().getId())
                            .build());
                })
                .flatMap(bestillingRepository::save)
                .flatMap(bestilling1 -> getBestillingProgresser(bestilling1)
                        .map(progresser -> {
                            bestilling1.setProgresser(progresser);
                            return bestilling1;
                        }));
    }

    @Transactional
// Egen transaksjon på denne da bestillingId hentes opp igjen fra database i samme kallet
    public Mono<Bestilling> createBestillingForGjenopprettFraIdent(String ident, List<String> miljoer) {

        return identRepository.findByIdent(ident)
                .switchIfEmpty(Mono.error(new NotFoundException(format("Testperson med ident %s ble ikke funnet.", ident))))
                .flatMap(testident -> Mono.zip(
                        Mono.just(testident),
                        fetchBruker(),
                        miljoerConsumer.getMiljoer()))
                .map(tuple -> Bestilling.builder()
                        .gruppeId(tuple.getT1().getGruppeId())
                        .ident(ident)
                        .antallIdenter(1)
                        .bestKriterier("{}")
                        .sistOppdatert(now())
                        .miljoer(filterAvailable(miljoer, tuple.getT3()))
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
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE + gruppeId)))
                .flatMap(testgruppe -> Mono.zip(
                        fetchBruker(),
                        identRepository.findByGruppeId(gruppeId, Pageable.unpaged())
                                .collectList(),
                        miljoerConsumer.getMiljoer()))
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
                        .miljoer(filterAvailable(miljoer, tuple.getT3()))
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
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE + gruppeId)))
                .doOnNext(testgruppe -> fixAaregAbstractClassProblem(request.getAareg()))
                .flatMap(testgruppe -> Mono.zip(
                        fetchBruker(),
                        miljoerConsumer.getMiljoer()))
                .map(tuple -> Bestilling.builder()
                        .gruppeId(gruppeId)
                        .kildeMiljoe("PDL")
                        .miljoer(filterAvailable(request.getEnvironments(), tuple.getT2()))
                        .sistOppdatert(now())
                        .bruker(tuple.getT1())
                        .brukerId(tuple.getT1().getId())
                        .antallIdenter(request.getIdenter().size())
                        .pdlImport(join(",", request.getIdenter()))
                        .beskrivelse(request.getBeskrivelse())
                        .build())
                .flatMap(bestillingRepository::save)
                .doOnNext(bestilling -> request.setId(bestilling.getId()))
                .flatMap(bestilling ->
                        getBestKriterier(request)
                                .flatMap(kriterier -> {
                                    bestilling.setBestKriterier(kriterier);
                                    return bestillingRepository.save(bestilling);
                                }))
                .flatMap(bestilling -> getBestillingProgresser(bestilling)
                        .map(progresser -> {
                            bestilling.setProgresser(progresser);
                            return bestilling;
                        }));
    }

    @Transactional
    public Mono<Bestilling> saveBestilling(Long gruppeId, RsDollyBestillingLeggTilPaaGruppe request) {

        return testgruppeRepository.findById(gruppeId)
                .switchIfEmpty(Mono.error(new NotFoundException(FINNES_IKKE + gruppeId)))
                .doOnNext(testgruppe -> fixAaregAbstractClassProblem(request.getAareg()))
                .flatMap(testgruppe -> Mono.zip(
                        fetchBruker(),
                        identRepository.countByGruppeId(gruppeId),
                        miljoerConsumer.getMiljoer()))
                .doOnNext(tuple -> log.info("Antall testidenter {} i gruppe {} ", tuple.getT2(), gruppeId))
                .map(tuple -> Bestilling.builder()
                        .gruppeId(gruppeId)
                        .miljoer(filterAvailable(request.getEnvironments(), tuple.getT3()))
                        .sistOppdatert(now())
                        .bruker(tuple.getT1())
                        .antallIdenter(tuple.getT2())
                        .navSyntetiskIdent(request.getNavSyntetiskIdent())
                        .build())
                .flatMap(bestillingRepository::save)
                .doOnNext(bestilling -> request.setId(bestilling.getId()))
                .flatMap(bestilling ->
                        getBestKriterier(request)
                                .flatMap(kriterier -> {
                                    bestilling.setBestKriterier(kriterier);
                                    return bestillingRepository.save(bestilling);
                                }))
                .flatMap(bestilling -> getBestillingProgresser(bestilling)
                        .map(progresser -> {
                            bestilling.setProgresser(progresser);
                            return bestilling;
                        }));
    }

    public Mono<Void> slettBestillingerByGruppeId(Long gruppeId) {

        return bestillingRepository.findByGruppeId(gruppeId)
                .map(Bestilling::getId)
                .doOnNext(elasticRepository::deleteById)
                .collectList()
                .then(bestillingKontrollRepository.deleteByGruppeId(gruppeId))
                .then(bestillingProgressRepository.deleteByGruppeId(gruppeId))
                .then(bestillingRepository.deleteByGruppeId(gruppeId))
                .then();
    }

    public Mono<Void> slettBestillingByBestillingId(Long bestillingId) {

        return bestillingProgressRepository.deleteByBestillingId(bestillingId)
                .then(bestillingKontrollRepository.deleteByBestillingWithNoChildren(bestillingId))
                .then(Mono.fromRunnable(() -> elasticRepository.deleteById(bestillingId)))
                .then(bestillingRepository.deleteById(bestillingId));
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

        return oppdaterDokarkivDokumenter(request)
                .flatMap(request1 -> oppdaterHistarkDokumenter(request1)
                        .flatMap(request2 -> Mono.just(
                                Objects.requireNonNull(toJson(BestilteKriterier.builder()
                                        .aareg(request2.getAareg())
                                        .arbeidsplassenCV(request2.getArbeidsplassenCV())
                                        .arbeidssoekerregisteret(request2.getArbeidssoekerregisteret())
                                        .arenaforvalter(request2.getArenaforvalter())
                                        .bankkonto(request2.getBankkonto())
                                        .brregstub(request2.getBrregstub())
                                        .dokarkiv(request2.getDokarkiv())
                                        .etterlatteYtelser(request2.getEtterlatteYtelser())
                                        .fullmakt(request2.getFullmakt())
                                        .histark(request2.getHistark())
                                        .inntektsmelding(request2.getInntektsmelding())
                                        .inntektstub(request2.getInntektstub())
                                        .instdata(request2.getInstdata())
                                        .krrstub(request2.getKrrstub())
                                        .medl(request2.getMedl())
                                        .pdldata(request2.getPdldata())
                                        .pensjonforvalter(request2.getPensjonforvalter())
                                        .sigrunstub(request2.getSigrunstub())
                                        .sigrunstubPensjonsgivende(request2.getSigrunstubPensjonsgivende())
                                        .sigrunstubSummertSkattegrunnlag(request2.getSigrunstubSummertSkattegrunnlag())
                                        .skattekort(request2.getSkattekort())
                                        .skjerming(request2.getSkjerming())
                                        .sykemelding(request2.getSykemelding())
                                        .tpsMessaging(request2.getTpsMessaging())
                                        .udistub(request2.getUdistub())
                                        .yrkesskader(request2.getYrkesskader())
                                        .build())))));
    }

    private Mono<RsDollyBestilling> oppdaterDokarkivDokumenter(RsDollyBestilling request) {

        return Flux.fromIterable(request.getDokarkiv())
                .flatMap(tema -> Flux.fromIterable(tema.getDokumenter())
                        .flatMap(dokument -> Flux.fromIterable(dokument.getDokumentvarianter())
                                .flatMap(dokumentVariant -> {
                                    if (isNotBlank(dokumentVariant.getFysiskDokument())) {
                                        return lagreDokument(dokumentVariant.getFysiskDokument(), request.getId(), DokumentType.BESTILLING_DOKARKIV)
                                                .map(id -> {
                                                    dokumentVariant.setDokumentReferanse(id);
                                                    dokumentVariant.setFysiskDokument(null);
                                                    return id;
                                                });
                                    }
                                    return Mono.just(0L);
                                })))
                .collectList()
                .thenReturn(request);
    }

    private Mono<RsDollyBestilling> oppdaterHistarkDokumenter(RsDollyBestilling request) {

        if (nonNull(request.getHistark())) {
            return Flux.fromIterable(request.getHistark().getDokumenter())
                    .flatMap(dokument -> {
                        if (isNotBlank(dokument.getFysiskDokument())) {
                            return lagreDokument(dokument.getFysiskDokument(), request.getId(), DokumentType.BESTILLING_HISTARK)
                                    .doOnNext(dokument::setDokumentReferanse)
                                    .doOnNext(id -> dokument.setFysiskDokument(null));
                        }
                        return Mono.empty();
                    })
                    .collectList()
                    .thenReturn(request);
        }
        return Mono.just(request);
    }

    private Mono<Long> lagreDokument(String dokument, Long bestillingId, DokumentType dokumentType) {

        return Mono.just(Dokument.builder()
                        .contents(dokument)
                        .bestillingId(bestillingId)
                        .dokumentType(dokumentType)
                        .sistOppdatert(now())
                        .build())
                .flatMap(dokumentRepository::save)
                .map(Dokument::getId);
    }

    private String filterAvailable(Collection<String> environments, Collection<String> available) {

        if (isNull(environments) || environments.isEmpty()) {
            return null;
        }

        if (isNull(available) || available.isEmpty()) {
            return null;
        }

        return environments.stream()
                .filter(available::contains)
                .collect(Collectors.joining(","));
    }

    private String filterAvailable(String miljoer, Collection<String> available) {

        return isNotBlank(miljoer) ? filterAvailable(Arrays.asList(miljoer.split(",")), available) : null;
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