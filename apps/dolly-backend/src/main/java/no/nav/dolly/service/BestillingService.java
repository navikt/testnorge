package no.nav.dolly.service;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingKontroll;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Bruker;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.BestilteKriterier;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyBestillingLeggTilPaaGruppe;
import no.nav.dolly.domain.resultset.RsDollyImportFraPdlRequest;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.entity.bestilling.RsBestillingFragment;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.StaleStateException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
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
import static no.nav.dolly.util.CurrentAuthentication.getUserId;
import static no.nav.dolly.util.DistinctByKeyUtil.distinctByKey;
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
    private final BestillingMalService bestillingMalService;
    private final BestillingKontrollRepository bestillingKontrollRepository;
    private final IdentRepository identRepository;
    private final BestillingProgressRepository bestillingProgressRepository;
    private final ObjectMapper objectMapper;
    private final TestgruppeRepository testgruppeRepository;
    private final BrukerService brukerService;
    private final GetUserInfo getUserInfo;

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
        return Stream.concat(
                        bestillingRepository.findByIdContaining(wrapSearchString(bestillingID)).stream(),
                        bestillingRepository.findByGruppenavnContaining(wrapSearchString(gruppeNavn)).stream())
                .filter(distinctByKey(RsBestillingFragment::getid))
                .toList();
    }

    @Transactional
    public Bestilling saveBestillingToDB(Bestilling bestilling) {
        try {
            return bestillingRepository.save(bestilling);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("Kunne ikke lagre bestilling: " + e.getMessage(), e);
        }
    }

    public Set<Bestilling> fetchBestillingerByGruppeId(Long gruppeId, Integer page, Integer pageSize) {
        Optional<Testgruppe> testgruppe = testgruppeRepository.findById(gruppeId);
        if (page == null || pageSize == null) {
            return testgruppe
                    .map(Testgruppe::getBestillinger).orElse(emptySet());
        }
        return testgruppe
                .map(value -> value.getBestillinger().stream()
                        .skip(page.longValue() * pageSize).limit(pageSize)
                        .collect(toSet()))
                .orElse(emptySet());
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
    @Retryable(StaleStateException.class)
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
                                    } catch (NoSuchMethodException | IllegalAccessException |
                                             InvocationTargetException e) {
                                        log.error("Oppdatering av bestilling {} feilet ved stopp-kommando {}",
                                                bestilling.getId(), e.getMessage(), e);
                                    }
                                }));
    }

    @Transactional
    public Bestilling saveBestilling(RsDollyUpdateRequest request, String ident) {

        Testident testident = identRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(format("Testident %s ble ikke funnet", ident)));
        var bruker = fetchOrCreateBruker();

        fixAaregAbstractClassProblem(request.getAareg());
        Bestilling bestilling = Bestilling.builder()
                .gruppe(testident.getTestgruppe())
                .ident(ident)
                .antallIdenter(1)
                .navSyntetiskIdent(request.getNavSyntetiskIdent())
                .sistOppdatert(now())
                .miljoer(join(",", request.getEnvironments()))
                .bestKriterier(getBestKriterier(request))
                .bruker(bruker)
                .build();

        if (isNotBlank(request.getMalBestillingNavn())) {
            bestillingMalService.saveBestillingMal(bestilling, request.getMalBestillingNavn(), bruker);
        }
        return saveBestillingToDB(bestilling);
    }

    @Transactional
    public Bestilling saveBestilling(Long gruppeId, RsDollyBestilling request, Integer antall,
                                     List<String> opprettFraIdenter, Boolean navSyntetiskIdent, String beskrivelse) {
        Testgruppe gruppe = testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException(NOT_FOUND + gruppeId));
        var bruker = fetchOrCreateBruker();
        Bestilling bestilling = Bestilling.builder()
                .gruppe(gruppe)
                .antallIdenter(antall)
                .navSyntetiskIdent(navSyntetiskIdent)
                .sistOppdatert(now())
                .miljoer(join(",", request.getEnvironments()))
                .bestKriterier(getBestKriterier(request))
                .opprettFraIdenter(nonNull(opprettFraIdenter) ? join(",", opprettFraIdenter) : null)
                .bruker(bruker)
                .beskrivelse(beskrivelse)
                .build();
        fixAaregAbstractClassProblem(request.getAareg());
        if (isNotBlank(request.getMalBestillingNavn())) {
            bestillingMalService.saveBestillingMal(bestilling, request.getMalBestillingNavn(), bruker);
        }
        return saveBestillingToDB(bestilling);
    }

    @Transactional
    // Egen transaksjon på denne da bestillingId hentes opp igjen fra database i samme kallet
    public Bestilling createBestillingForGjenopprettFraBestilling(Long bestillingId, List<String> miljoer) {
        Bestilling bestilling = fetchBestillingById(bestillingId);
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
                        .miljoer(isNull(miljoer) || miljoer.isEmpty() ? bestilling.getMiljoer() : join(",", miljoer))
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
                        .miljoer(isNull(miljoer) || miljoer.isEmpty() ? "" : join(",", miljoer))
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
                        .miljoer(miljoer)
                        .opprettetFraGruppeId(gruppeId)
                        .bruker(fetchOrCreateBruker())
                        .build());
    }

    @Transactional
    public Bestilling saveBestilling(Long gruppeId, RsDollyImportFraPdlRequest request) {

        Testgruppe gruppe = testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException(NOT_FOUND + gruppeId));
        var bruker = fetchOrCreateBruker();
        Bestilling bestilling = Bestilling.builder()
                .gruppe(gruppe)
                .kildeMiljoe("PDL")
                .miljoer(join(",", request.getEnvironments()))
                .sistOppdatert(now())
                .bruker(bruker)
                .antallIdenter(request.getIdenter().size())
                .bestKriterier(getBestKriterier(request))
                .pdlImport(join(",", request.getIdenter()))
                .build();

        fixAaregAbstractClassProblem(request.getAareg());
        if (isNotBlank(request.getMalBestillingNavn())) {
            bestillingMalService.saveBestillingMal(bestilling, request.getMalBestillingNavn(), bruker);
        }
        return saveBestillingToDB(bestilling);
    }

    @Transactional
    public Bestilling saveBestilling(Long gruppeId, RsDollyBestillingLeggTilPaaGruppe request) {

        var gruppe = testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException(NOT_FOUND + gruppeId));
        var size = identRepository.countByTestgruppe(gruppeId);
        log.info("Antall testidenter {} i gruppe {} ", size, gruppeId);
        fixAaregAbstractClassProblem(request.getAareg());
        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(gruppe)
                        .miljoer(join(",", request.getEnvironments()))
                        .sistOppdatert(now())
                        .bruker(fetchOrCreateBruker())
                        .antallIdenter(size)
                        .navSyntetiskIdent(request.getNavSyntetiskIdent())
                        .bestKriterier(getBestKriterier(request))
                        .build());
    }

    public void slettBestillingerByGruppeId(Long gruppeId) {

        bestillingKontrollRepository.deleteByGruppeId(gruppeId);
        bestillingProgressRepository.deleteByGruppeId(gruppeId);
        bestillingRepository.deleteByGruppeId(gruppeId);
        bestillingRepository.updateBestillingNullifyGruppe(gruppeId);
    }

    public void slettBestillingByBestillingId(Long bestillingId) {

        bestillingProgressRepository.deleteByBestilling_Id(bestillingId);
        bestillingKontrollRepository.deleteByBestillingWithNoChildren(bestillingId);
        bestillingRepository.deleteById(bestillingId);
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
        });
    }

    public List<Long> fetchBestillingerByTestident(String ident) {

        var bestillinger = bestillingRepository.findBestillingerByIdent(ident);
        return bestillinger.stream().map(Bestilling::getId).toList();
    }


    @Transactional
    public void swapIdent(String oldIdent, String newIdent) {
        bestillingRepository.swapIdent(oldIdent, newIdent);
    }

    public String getBestKriterier(RsDollyBestilling request) {
        return toJson(BestilteKriterier.builder()
                .aareg(request.getAareg())
                .krrstub(request.getKrrstub())
                .udistub(request.getUdistub())
                .sigrunstub(request.getSigrunstub())
                .arenaforvalter(request.getArenaforvalter())
                .pdldata(request.getPdldata())
                .instdata(request.getInstdata())
                .inntektstub(request.getInntektstub())
                .pensjonforvalter(request.getPensjonforvalter())
                .inntektsmelding(request.getInntektsmelding())
                .brregstub(request.getBrregstub())
                .dokarkiv(request.getDokarkiv())
                .medl(request.getMedl())
                .histark(request.getHistark())
                .tpsMessaging(request.getTpsMessaging())
                .bankkonto(request.getBankkonto())
                .skjerming(request.getSkjerming())
                .sykemelding(request.getSykemelding())
                .arbeidsplassenCV(request.getArbeidsplassenCV())
                .build());
    }

    private String wrapSearchString(String searchString) {
        return isNotBlank(searchString) ? "%%%s%%".formatted(searchString) : "";
    }

    private Bruker fetchOrCreateBruker() {
        return brukerService.fetchOrCreateBruker(getUserId(getUserInfo));
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