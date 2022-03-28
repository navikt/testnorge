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
import no.nav.dolly.domain.resultset.RsDollyImportFraTpsRequest;
import no.nav.dolly.domain.resultset.RsDollyRelasjonRequest;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.aareg.RsAareg;
import no.nav.dolly.domain.resultset.aareg.RsOrganisasjon;
import no.nav.dolly.domain.resultset.pdlforvalter.RsPdldata;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBasisBestilling;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.repository.TestgruppeRepository;
import no.nav.testnav.libs.servletsecurity.action.GetUserInfo;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.LocalDateTime.now;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.Objects.nonNull;
import static java.util.stream.Collectors.toSet;
import static net.logstash.logback.util.StringUtils.isBlank;
import static no.nav.dolly.util.CurrentAuthentication.getUserId;

@Slf4j
@Service
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@RequiredArgsConstructor
public class BestillingService {

    private final BestillingRepository bestillingRepository;
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

    public List<Bestilling> fetchMalbestillingByNavnAndUser(String brukerId, String malNavn) {
        Bruker bruker = brukerService.fetchBruker(brukerId);
        var bestillinger = nonNull(malNavn)
                ? bestillingRepository.findMalBestillingByMalnavnAndUser(bruker, malNavn)
                : bestillingRepository.findMalBestillingByUser(bruker);
        return bestillinger.orElse(emptyList());
    }

    @Transactional
    public Bestilling saveBestillingToDB(Bestilling bestilling) {
        try {
            overskrivDuplikateMalbestillinger(bestilling);
            return bestillingRepository.save(bestilling);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("Kunne ikke lagre bestilling: " + e.getMessage(), e);
        }
    }

    public Set<Bestilling> fetchBestillingerByGruppeId(Long gruppeId) {
        Optional<Testgruppe> testgruppe = testgruppeRepository.findById(gruppeId);
        return testgruppe.isPresent() ? testgruppe.get().getBestillinger() : emptySet();
    }

    public List<Bestilling> fetchMalBestillinger() {
        return bestillingRepository.findMalBestilling().orElse(emptyList());
    }

    @Transactional
    public Bestilling cancelBestilling(Long bestillingId) {
        Optional<BestillingKontroll> bestillingKontroll = bestillingKontrollRepository.findByBestillingId(bestillingId);
        if (!bestillingKontroll.isPresent()) {
            bestillingKontrollRepository.save(BestillingKontroll.builder()
                    .bestillingId(bestillingId)
                    .stoppet(true)
                    .build());
        }

        Bestilling bestilling = fetchBestillingById(bestillingId);
        bestilling.setStoppet(true);
        bestilling.setFerdig(true);
        bestilling.setSistOppdatert(now());
        bestilling.setBruker(fetchOrCreateBruker());
        saveBestillingToDB(bestilling);
        return bestilling;
    }

    public boolean isStoppet(Long bestillingId) {
        return bestillingKontrollRepository.findByBestillingId(bestillingId)
                .orElse(BestillingKontroll.builder().stoppet(false).build()).isStoppet();
    }

    @Transactional
    public Bestilling saveBestilling(String ident, RsDollyRelasjonRequest request) {

        Testident testident = identRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(format("Testident %s ble ikke funnet", ident)));

        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(testident.getTestgruppe())
                        .ident(ident)
                        .antallIdenter(1)
                        .sistOppdatert(now())
                        .miljoer(join(",", request.getEnvironments()))
                        .tpsfKriterier(toJson(request.getTpsf()))
                        .bestKriterier("{}")
                        .bruker(fetchOrCreateBruker())
                        .build());
    }

    @Transactional
    public Bestilling saveBestilling(RsDollyUpdateRequest request, String ident) {

        Testident testident = identRepository.findByIdent(ident)
                .orElseThrow(() -> new NotFoundException(format("Testident %s ble ikke funnet", ident)));

        fixAaregAbstractClassProblem(request.getAareg());
        fixPdlAbstractClassProblem(request.getPdlforvalter());
        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(testident.getTestgruppe())
                        .ident(ident)
                        .antallIdenter(1)
                        .navSyntetiskIdent(request.getNavSyntetiskIdent())
                        .sistOppdatert(now())
                        .miljoer(join(",", request.getEnvironments()))
                        .tpsfKriterier(toJson(request.getTpsf()))
                        .bestKriterier(getBestKriterier(request))
                        .malBestillingNavn(request.getMalBestillingNavn())
                        .bruker(fetchOrCreateBruker())
                        .build());
    }

    @Transactional
    public Bestilling saveBestilling(Long gruppeId, RsDollyBestilling request, RsTpsfBasisBestilling tpsf, Integer antall,
                                     List<String> opprettFraIdenter, Boolean navSyntetiskIdent, String beskrivelse) {
        Testgruppe gruppe = testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException("Finner ikke gruppe basert på gruppeID: " + gruppeId));
        fixAaregAbstractClassProblem(request.getAareg());
        fixPdlAbstractClassProblem(request.getPdlforvalter());
        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(gruppe)
                        .antallIdenter(antall)
                        .navSyntetiskIdent(navSyntetiskIdent)
                        .sistOppdatert(now())
                        .miljoer(join(",", request.getEnvironments()))
                        .tpsfKriterier(toJson(tpsf))
                        .bestKriterier(getBestKriterier(request))
                        .opprettFraIdenter(nonNull(opprettFraIdenter) ? join(",", opprettFraIdenter) : null)
                        .malBestillingNavn(request.getMalBestillingNavn())
                        .bruker(fetchOrCreateBruker())
                        .beskrivelse(beskrivelse)
                        .tags(gruppe.getTags())
                        .build());
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
                        .miljoer(miljoer.isEmpty() ? bestilling.getMiljoer() : join(",", miljoer))
                        .opprettetFraId(bestillingId)
                        .tpsfKriterier(bestilling.getTpsfKriterier())
                        .bestKriterier(bestilling.getBestKriterier())
                        .bruker(fetchOrCreateBruker())
                        .build());
    }

    @Transactional
    // Egen transaksjon på denne da bestillingId hentes opp igjen fra database i samme kallet
    public Bestilling createBestillingForGjenopprettFraGruppe(Long gruppeId, String miljoer) {

        Optional<Testgruppe> testgruppe = testgruppeRepository.findById(gruppeId);

        if (!testgruppe.isPresent() || testgruppe.get().getTestidenter().isEmpty()) {
            throw new NotFoundException(format("Ingen testpersoner funnet i gruppe: %d", gruppeId));
        }

        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(testgruppe.get())
                        .antallIdenter(testgruppe.get().getTestidenter().size())
                        .tpsfKriterier("{}")
                        .bestKriterier("{}")
                        .sistOppdatert(now())
                        .miljoer(miljoer)
                        .opprettetFraGruppeId(gruppeId)
                        .bruker(fetchOrCreateBruker())
                        .build());
    }

    @Transactional
    public Bestilling saveBestilling(Long gruppeId, RsDollyImportFraTpsRequest request) {

        Testgruppe gruppe = testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException("Finner ikke gruppe basert på gruppeID: " + gruppeId));
        fixAaregAbstractClassProblem(request.getAareg());
        fixPdlAbstractClassProblem(request.getPdlforvalter());
        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(gruppe)
                        .kildeMiljoe(request.getKildeMiljoe())
                        .miljoer(join(",", request.getEnvironments()))
                        .sistOppdatert(now())
                        .bruker(fetchOrCreateBruker())
                        .antallIdenter(request.getIdenter().size())
                        .bestKriterier(getBestKriterier(request))
                        .tpsImport(join(",", request.getIdenter()))
                        .build());
    }

    @Transactional
    public Bestilling saveBestilling(Long gruppeId, RsDollyImportFraPdlRequest request) {

        Testgruppe gruppe = testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException("Finner ikke gruppe basert på gruppeID: " + gruppeId));
        fixAaregAbstractClassProblem(request.getAareg());
        fixPdlAbstractClassProblem(request.getPdlforvalter());
        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(gruppe)
                        .kildeMiljoe("PDL")
                        .miljoer(join(",", request.getEnvironments()))
                        .sistOppdatert(now())
                        .bruker(fetchOrCreateBruker())
                        .antallIdenter(request.getIdenter().size())
                        .bestKriterier(getBestKriterier(request))
                        .pdlImport(join(",", request.getIdenter()))
                        .build());
    }

    @Transactional
    public Bestilling saveBestilling(Long gruppeId, RsDollyBestillingLeggTilPaaGruppe request) {

        Testgruppe gruppe = testgruppeRepository.findById(gruppeId).orElseThrow(() -> new NotFoundException("Finner ikke gruppe basert på gruppeID: " + gruppeId));
        fixAaregAbstractClassProblem(request.getAareg());
        fixPdlAbstractClassProblem(request.getPdlforvalter());
        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(gruppe)
                        .miljoer(join(",", request.getEnvironments()))
                        .sistOppdatert(now())
                        .bruker(fetchOrCreateBruker())
                        .antallIdenter(gruppe.getTestidenter().size())
                        .navSyntetiskIdent(request.getNavSyntetiskIdent())
                        .bestKriterier(getBestKriterier(request))
                        .build());
    }

    @Transactional
    public void redigerBestilling(Long id, String malbestillingNavn) {

        Optional<Bestilling> token = bestillingRepository.findById(id);
        Bestilling bestilling = token.orElseThrow(() -> new NotFoundException(format("Id {%d} ikke funnet ", id)));
        bestilling.setMalBestillingNavn(malbestillingNavn);
    }

    public void slettBestillingerByGruppeId(Long gruppeId) {

        bestillingKontrollRepository.deleteByGruppeId(gruppeId);
        bestillingProgressRepository.deleteByGruppeId(gruppeId);
        bestillingRepository.deleteByGruppeId(gruppeId);
    }

    public void slettBestillingByTestIdent(String ident) {

        List<BestillingProgress> bestillingProgresses = bestillingProgressRepository.findByIdent(ident);
        bestillingProgressRepository.deleteByIdent(ident);

        Set<Long> bestillingIds = bestillingProgresses.stream()
                .map(BestillingProgress::getBestilling)
                .map(Bestilling::getId)
                .collect(toSet());

        bestillingIds.forEach(id -> {

            bestillingKontrollRepository.deleteByBestillingWithNoChildren(id);
            bestillingRepository.deleteBestillingWithNoChildren(id);
        });
    }

    @Transactional
    public void swapIdent(String oldIdent, String newIdent) {
        bestillingRepository.swapIdent(oldIdent, newIdent);
    }

    private void overskrivDuplikateMalbestillinger(Bestilling bestilling) {
        if (isBlank(bestilling.getMalBestillingNavn())) {
            return;
        }
        List<Bestilling> gamleMalBestillinger = fetchMalbestillingByNavnAndUser(bestilling.getBruker().getBrukerId(), bestilling.getMalBestillingNavn());
        if (!gamleMalBestillinger.isEmpty()) {
            gamleMalBestillinger.forEach(malBestilling -> {
                malBestilling.setMalBestillingNavn(null);
                saveBestillingToDB(malBestilling);
            });
        }
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

    private String getBestKriterier(RsDollyBestilling request) {
        return toJson(BestilteKriterier.builder()
                .aareg(request.getAareg())
                .krrstub(request.getKrrstub())
                .udistub(request.getUdistub())
                .sigrunstub(request.getSigrunstub())
                .arenaforvalter(request.getArenaforvalter())
                .pdlforvalter(request.getPdlforvalter())
                .pdldata(request.getPdldata())
                .instdata(request.getInstdata())
                .inntektstub(request.getInntektstub())
                .pensjonforvalter(request.getPensjonforvalter())
                .inntektsmelding(request.getInntektsmelding())
                .brregstub(request.getBrregstub())
                .dokarkiv(request.getDokarkiv())
                .tpsMessaging(request.getTpsMessaging())
                .skjerming(request.getSkjerming())
                .sykemelding(request.getSykemelding())
                .build());
    }

    private static void fixAaregAbstractClassProblem(List<RsAareg> aaregdata) {

        aaregdata.forEach(arbeidforhold -> {
            if (nonNull(arbeidforhold.getArbeidsgiver())) {
                arbeidforhold.getArbeidsgiver().setAktoertype(
                        arbeidforhold.getArbeidsgiver() instanceof RsOrganisasjon ? "ORG" : "PERS");
            }
        });
    }

    private static void fixPdlAbstractClassProblem(RsPdldata pdldata) {

        if (nonNull(pdldata)) {
            if (nonNull(pdldata.getKontaktinformasjonForDoedsbo())) {
                pdldata.getKontaktinformasjonForDoedsbo().setAdressat(pdldata.getKontaktinformasjonForDoedsbo().getAdressat());
            }
            if (nonNull(pdldata.getFalskIdentitet())) {
                pdldata.getFalskIdentitet().setRettIdentitet(pdldata.getFalskIdentitet().getRettIdentitet());
            }
        }
    }
}