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
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.BestilteKriterier;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingLeggTilPaaGruppe;
import no.nav.dolly.domain.resultset.RsDollyImportFraPdlRequest;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingFragment;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.DokumentRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptySet;
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
    private final BestillingRepository bestillingRepository;
    private final MalBestillingService malBestillingService;
    private final BestillingKontrollRepository bestillingKontrollRepository;
    private final IdentRepository identRepository;
    private final BestillingProgressRepository bestillingProgressRepository;
    private final ObjectMapper objectMapper;
    private final TestgruppeRepository testgruppeRepository;
    private final BrukerService brukerService;
    private final BestillingElasticRepository elasticRepository;
    private final MiljoerConsumer miljoerConsumer;
    private final DokumentRepository dokumentRepository;

    public Bestilling fetchBestillingById(Long bestillingId) {
        return bestillingRepository.findById(bestillingId)
                .orElseThrow(() -> new NotFoundException(format("Fant ikke bestillingId %d", bestillingId)));
    }

    public List<RsBestillingFragment> fetchBestillingByFragment(String bestillingFragment) {
        var searchQueries = bestillingFragment.split(" ");
        String bestillingID = Arrays.stream(searchQueries)
                .filter(word -> word.matches("\\d+"))
                .findFirst()
                .orElse("");
        String gruppeNavn = Arrays.stream(searchQueries)
                .filter(word -> !word.equals(bestillingID))
                .collect(Collectors.joining(" "));
        if (isNoneBlank(gruppeNavn) && isNoneBlank(bestillingID)) {
            return bestillingRepository.findByIdContainingAndGruppeNavnContaining(bestillingID, gruppeNavn);
        } else {
            return Stream.concat(
                            bestillingRepository.findByIdContaining(wrapSearchString(bestillingID)).stream(),
                            bestillingRepository.findByGruppenavnContaining(wrapSearchString(gruppeNavn)).stream())
                    .filter(distinctByKey(RsBestillingFragment::getid))
                    .toList();
        }
    }

    @Transactional
    public Bestilling saveBestillingToDB(Bestilling bestilling) {
        try {
            return bestillingRepository.save(bestilling);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("Kunne ikke lagre bestilling: " + e.getMessage(), e);
        }
    }

    public Set<Bestilling> fetchBestillingerByGruppeIdOgIkkeFerdig(Long gruppeId) {
        Optional<Testgruppe> testgruppe = testgruppeRepository.findById(gruppeId);
        return testgruppe
                .map(value -> value.getBestillinger().stream()
                        .filter(b -> !b.isFerdig())
                        .collect(toSet()))
                .orElse(emptySet());
    }

    public Set<String> fetchBestilteMiljoerByGruppeId(Long gruppeId) {
        Optional<Testgruppe> testgruppe = testgruppeRepository.findById(gruppeId);
        return testgruppe.map(value -> value.getBestillinger().stream()
                .map(Bestilling::getMiljoer)
                .filter(StringUtils::isNotBlank)
                .flatMap(miljoer -> Arrays.stream(miljoer.split(",")))
                .filter(StringUtils::isNotBlank)
                .collect(toSet())).orElse(emptySet());
    }

    public Optional<Integer> getPaginertBestillingIndex(Long bestillingId, Long gruppeId) {

        return bestillingRepository.getPaginertBestillingIndex(bestillingId, gruppeId);
    }

    public Page<Bestilling> getBestillingerFromGruppeIdPaginert(Long gruppeId, Integer pageNo, Integer pageSize) {

        return bestillingRepository
                .getBestillingerFromGruppeId(gruppeId, PageRequest.of(pageNo, pageSize));
    }

    @Transactional
    @Retryable
    public Bestilling cancelBestilling(Long bestillingId) {

        var bestillingKontroll = bestillingKontrollRepository.findByBestillingId(bestillingId);
        if (bestillingKontroll.isEmpty()) {
            bestillingKontrollRepository.save(BestillingKontroll.builder()
                    .bestillingId(bestillingId)
                    .stoppet(true)
                    .build());
        }

        var bestilling = fetchBestillingById(bestillingId);
        bestilling.setStoppet(true);
        bestilling.setFerdig(true);
        bestilling.setSistOppdatert(now());
        bestilling.setBruker(fetchOrCreateBruker());

        return bestilling;
    }

    public boolean isStoppet(Long bestillingId) {

        return bestillingKontrollRepository.findByBestillingId(bestillingId)
                .orElse(BestillingKontroll.builder().stoppet(false).build()).isStoppet();
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
    public Bestilling saveBestilling(RsDollyUpdateRequest request, String ident) {

        var testident = identRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(format("Testident %s ble ikke funnet", ident)));
        var bruker = fetchOrCreateBruker();

        fixAaregAbstractClassProblem(request.getAareg());
        var bestilling = saveBestillingToDB(Bestilling.builder()
                .gruppe(testident.getTestgruppe())
                .ident(ident)
                .antallIdenter(1)
                .navSyntetiskIdent(request.getNavSyntetiskIdent())
                .sistOppdatert(now())
                .miljoer(filterAvailable(request.getEnvironments()))
                .bruker(bruker)
                .build());

        request.setId(bestilling.getId());
        bestilling.setBestKriterier(getBestKriterier(request));

        if (isNotBlank(request.getMalBestillingNavn())) {
            malBestillingService.saveBestillingMal(bestilling, request.getMalBestillingNavn(), bruker);
        }
        return bestilling;
    }

    @Transactional
    public Bestilling saveBestilling(Long gruppeId, RsDollyBestilling request, Integer antall,
                                     List<String> opprettFraIdenter, Boolean navSyntetiskIdent, String beskrivelse) {
        var gruppe = testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException(NOT_FOUND + gruppeId));
        fixAaregAbstractClassProblem(request.getAareg());

        var bruker = fetchOrCreateBruker();
        var bestilling = saveBestillingToDB(Bestilling.builder()
                .gruppe(gruppe)
                .antallIdenter(antall)
                .navSyntetiskIdent(navSyntetiskIdent)
                .sistOppdatert(now())
                .miljoer(filterAvailable(request.getEnvironments()))
                .opprettFraIdenter(nonNull(opprettFraIdenter) ? join(",", opprettFraIdenter) : null)
                .bruker(bruker)
                .beskrivelse(beskrivelse)
                .build());

        request.setId(bestilling.getId());
        bestilling.setBestKriterier(getBestKriterier(request));

        if (isNotBlank(request.getMalBestillingNavn())) {
            malBestillingService.saveBestillingMal(bestilling, request.getMalBestillingNavn(), bruker);
        }
        return bestilling;
    }

    @Transactional
    // Egen transaksjon på denne da bestillingId hentes opp igjen fra database i samme kallet
    public Bestilling createBestillingForGjenopprettFraBestilling(Long bestillingId, String miljoer) {
        var bestilling = fetchBestillingById(bestillingId);
        if (!bestilling.isFerdig()) {
            throw new DollyFunctionalException(format("Du kan ikke starte gjenopprett før bestilling %d er ferdigstilt.", bestillingId));
        }
        if (bestilling.getGruppe().getTestidenter().isEmpty()) {
            throw new NotFoundException(format("Ingen testidenter funnet på bestilling: %d", bestillingId));
        }
        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(bestilling.getGruppe())
                        .antallIdenter(bestilling.getAntallIdenter())
                        .opprettFraIdenter(bestilling.getOpprettFraIdenter())
                        .sistOppdatert(now())
                        .miljoer(filterAvailable(isNotBlank(miljoer) ? miljoer : bestilling.getMiljoer()))
                        .opprettetFraId(bestillingId)
                        .bestKriterier("{}")
                        .bruker(fetchOrCreateBruker())
                        .build());
    }

    @Transactional
    // Egen transaksjon på denne da bestillingId hentes opp igjen fra database i samme kallet
    public Bestilling createBestillingForGjenopprettFraIdent(String ident, Testgruppe testgruppe, List<String> miljoer) {

        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(testgruppe)
                        .ident(ident)
                        .antallIdenter(1)
                        .bestKriterier("{}")
                        .sistOppdatert(now())
                        .miljoer(filterAvailable(miljoer))
                        .gjenopprettetFraIdent(ident)
                        .bruker(fetchOrCreateBruker())
                        .build());
    }

    @Transactional
    // Egen transaksjon på denne da bestillingId hentes opp igjen fra database i samme kallet
    public Bestilling createBestillingForGjenopprettFraGruppe(Long gruppeId, String miljoer) {

        Optional<Testgruppe> testgruppe = testgruppeRepository.findById(gruppeId);

        if (testgruppe.isEmpty() || testgruppe.get().getTestidenter().isEmpty()) {
            throw new NotFoundException(format("Ingen testpersoner funnet i gruppe: %d", gruppeId));
        }

        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(testgruppe.get())
                        .antallIdenter(testgruppe.get().getTestidenter().size())
                        .bestKriterier("{}")
                        .sistOppdatert(now())
                        .miljoer(filterAvailable(miljoer))
                        .opprettetFraGruppeId(gruppeId)
                        .bruker(fetchOrCreateBruker())
                        .build());
    }

    @Transactional
    public Bestilling saveBestilling(Long gruppeId, RsDollyImportFraPdlRequest request) {

        var gruppe = testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException(NOT_FOUND + gruppeId));
        var bruker = fetchOrCreateBruker();
        fixAaregAbstractClassProblem(request.getAareg());

        var bestilling = saveBestillingToDB(Bestilling.builder()
                .gruppe(gruppe)
                .kildeMiljoe("PDL")
                .miljoer(filterAvailable(request.getEnvironments()))
                .sistOppdatert(now())
                .bruker(bruker)
                .antallIdenter(request.getIdenter().size())
                .pdlImport(join(",", request.getIdenter()))
                .beskrivelse(request.getBeskrivelse())
                .build());

        request.setId(bestilling.getId());
        bestilling.setBestKriterier(getBestKriterier(request));

        if (isNotBlank(request.getMalBestillingNavn())) {
            malBestillingService.saveBestillingMal(bestilling, request.getMalBestillingNavn(), bruker);
        }
        return bestilling;
    }

    @Transactional
    public Bestilling saveBestilling(Long gruppeId, RsDollyBestillingLeggTilPaaGruppe request) {

        var gruppe = testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException(NOT_FOUND + gruppeId));
        var size = identRepository.countByTestgruppe(gruppeId);
        log.info("Antall testidenter {} i gruppe {} ", size, gruppeId);
        fixAaregAbstractClassProblem(request.getAareg());

        var bestilling = saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(gruppe)
                        .miljoer(filterAvailable(request.getEnvironments()))
                        .sistOppdatert(now())
                        .bruker(fetchOrCreateBruker())
                        .antallIdenter(size)
                        .navSyntetiskIdent(request.getNavSyntetiskIdent())
                        .build());

        request.setId(bestilling.getId());
        bestilling.setBestKriterier(getBestKriterier(request));

        return bestilling;
    }

    public void slettBestillingerByGruppeId(Long gruppeId) {

        testgruppeRepository.findById(gruppeId).stream()
                .map(Testgruppe::getBestillinger)
                .flatMap(Collection::stream)
                .map(Bestilling::getId)
                .forEach(elasticRepository::deleteById);

        bestillingKontrollRepository.deleteByGruppeId(gruppeId);
        bestillingProgressRepository.deleteByGruppeId(gruppeId);
        bestillingRepository.deleteByGruppeId(gruppeId);
        bestillingRepository.updateBestillingNullifyGruppe(gruppeId);
    }

    public void slettBestillingByBestillingId(Long bestillingId) {

        bestillingProgressRepository.deleteByBestilling_Id(bestillingId);
        bestillingKontrollRepository.deleteByBestillingWithNoChildren(bestillingId);
        bestillingRepository.deleteById(bestillingId);
        elasticRepository.deleteById(bestillingId);
    }

    public void slettBestillingByTestIdent(String ident) {

        var bestillingProgresses = bestillingProgressRepository.findByIdent(ident);
        bestillingProgressRepository.deleteByIdent(ident);

        var bestillingIds = bestillingProgresses.stream()
                .map(BestillingProgress::getBestilling)
                .map(Bestilling::getId)
                .collect(toSet());

        bestillingIds.forEach(id -> {

            bestillingKontrollRepository.deleteByBestillingWithNoChildren(id);
            bestillingRepository.deleteBestillingWithNoChildren(id);
            elasticRepository.deleteById(id);
        });
    }

    @Transactional
    public void swapIdent(String oldIdent, String newIdent) {
        bestillingRepository.swapIdent(oldIdent, newIdent);
    }

    public String getBestKriterier(RsDollyBestilling request) {

        lagreDokumenter(request);
        return toJson(BestilteKriterier.builder()
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
                .build());
    }

    public List<BestillingProgress> getProgressByBestillingId(Long bestillingId) {

        return bestillingProgressRepository.findByBestilling_Id(bestillingId);
    }

    private void lagreDokumenter(RsDollyBestilling request) {

        request.getDokarkiv()
                .forEach(tema -> tema
                        .getDokumenter().forEach(dokument ->
                                dokument.getDokumentvarianter().forEach(dokumentVariant -> {
                                    if (isNotBlank(dokumentVariant.getFysiskDokument())) {
                                        dokumentVariant.setDokumentReferanse(lagreDokument(dokumentVariant.getFysiskDokument(), request.getId(), DokumentType.BESTILLING_DOKARKIV));
                                        dokumentVariant.setFysiskDokument(null);
                                    }
                                })));

        if (nonNull(request.getHistark())) {
            request.getHistark().getDokumenter()
                    .forEach(dokument -> {
                        if (isNotBlank(dokument.getFysiskDokument())) {
                            dokument.setDokumentReferanse(lagreDokument(dokument.getFysiskDokument(), request.getId(), DokumentType.BESTILLING_HISTARK));
                            dokument.setFysiskDokument(null);
                        }
                    });
        }
    }

    private Long lagreDokument(String dokument, Long bestillingId, DokumentType dokumentType) {

        return dokumentRepository
                .save(Dokument.builder()
                        .contents(dokument)
                        .bestillingId(bestillingId)
                        .dokumentType(dokumentType)
                        .build())
                .getId();
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

    private Bruker fetchOrCreateBruker() {
        return brukerService.fetchOrCreateBruker();
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
}