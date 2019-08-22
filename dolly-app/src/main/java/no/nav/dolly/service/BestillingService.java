package no.nav.dolly.service;

import static java.lang.String.format;
import static java.lang.String.join;
import static java.time.LocalDateTime.now;
import static java.util.Objects.nonNull;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.BestKriterier;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingKontroll;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestilling;
import no.nav.dolly.domain.resultset.RsDollyUpdateRequest;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBasisBestilling;
import no.nav.dolly.exceptions.ConstraintViolationException;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.exceptions.NotFoundException;
import no.nav.dolly.repository.BestillingKontrollRepository;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.BestillingRepository;
import no.nav.dolly.repository.IdentRepository;

@Service
@Slf4j
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class BestillingService {

    @Autowired
    private BestillingRepository bestillingRepository;

    @Autowired
    private TestgruppeService testgruppeService;

    @Autowired
    private BestillingKontrollRepository bestillingKontrollRepository;

    @Autowired
    private IdentRepository identRepository;

    @Autowired
    private BestillingProgressRepository bestillingProgressRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public Bestilling fetchBestillingById(Long bestillingId) {
        return bestillingRepository.findById(bestillingId).orElseThrow(() -> new NotFoundException(format("Fant ikke bestillingId %d", bestillingId)));
    }

    @Transactional
    public Bestilling saveBestillingToDB(Bestilling bestilling) {
        try {
            return bestillingRepository.save(bestilling);
        } catch (DataIntegrityViolationException e) {
            throw new ConstraintViolationException("Kunne ikke lagre bestilling: " + e.getMessage(), e);
        }
    }

    public List<Bestilling> fetchBestillingerByGruppeId(Long gruppeId) {
        return bestillingRepository.findBestillingByGruppeOrderById(testgruppeService.fetchTestgruppeById(gruppeId));
    }

    public List<Bestilling> fetchMalBestillinger() {
        return bestillingRepository.findMalBestilling().orElseThrow(() -> new NotFoundException("Ingen mal-bestilling funnet"));
    }

    @Transactional
    public Bestilling cancelBestilling(Long bestillingId) {
        Optional<BestillingKontroll> bestillingKontroll = bestillingKontrollRepository.findByBestillingIdOrderByBestillingId(bestillingId);
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
        saveBestillingToDB(bestilling);
        identRepository.deleteTestidentsByBestillingId(bestillingId);
        bestillingProgressRepository.deleteByBestillingId(bestillingId);
        return bestilling;
    }

    public boolean isStoppet(Long bestillingId) {
        return bestillingKontrollRepository.findByBestillingIdOrderByBestillingId(bestillingId).orElse(BestillingKontroll.builder().stoppet(false).build()).isStoppet();
    }

    @Transactional
    public Bestilling saveBestilling(String ident, RsDollyUpdateRequest request) {
        return saveBestillingToDB(
                Bestilling.builder()
                        .ident(ident)
                        .sistOppdatert(now())
                        .miljoer(join(",", request.getEnvironments()))
                        .tpsfKriterier(toJson(request.getTpsfPerson()))
                        .bestKriterier(toJson(BestKriterier.builder()
                                .aareg(request.getAareg())
                                .krrstub(request.getKrrstub())
                                .sigrunstub(request.getSigrunstub())
                                .arenaforvalter(request.getArenaforvalter())
                                .pdlforvalter(request.getPdlforvalter())
                                .instdata(request.getInstdata())
                                .build()))
                        .build());
    }

    @Transactional
    public Bestilling saveBestilling(Long gruppeId, RsDollyBestilling request, RsTpsfBasisBestilling tpsf, Integer antall, List<String> opprettFraIdenter) {
        Testgruppe gruppe = testgruppeService.fetchTestgruppeById(gruppeId);
        return saveBestillingToDB(
                Bestilling.builder()
                        .gruppe(gruppe)
                        .antallIdenter(antall)
                        .sistOppdatert(now())
                        .miljoer(join(",", request.getEnvironments()))
                        .tpsfKriterier(toJson(tpsf))
                        .bestKriterier(toJson(BestKriterier.builder()
                                .aareg(request.getAareg())
                                .krrStub(request.getKrrstub())
                                .udiStub(request.getUdistub())
                                .sigrunStub(request.getSigrunstub())
                                .arenaforvalter(request.getArenaforvalter())
                                .pdlforvalter(request.getPdlforvalter())
                                .instdata(request.getInstdata())
                                .build()))
                        .opprettFraIdenter(nonNull(opprettFraIdenter) ? join(",", opprettFraIdenter) : null)
                        .malBestillingNavn(request.getMalBestillingNavn())
                        .build());
    }

    @Transactional
    // Egen transaksjon på denne da bestillingId hentes opp igjen fra database i samme kallet
    public Bestilling createBestillingForGjenopprett(Long bestillingId, List<String> miljoer) {
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
                        .build()
        );
    }

    private String toJson(Object object) {
        try {
            if (nonNull(object)) {
                return objectMapper.writer().writeValueAsString(object);
            }
        } catch (JsonProcessingException | RuntimeException e) {
            log.debug("Konvertering til Json feilet", e);
        }
        return null;
    }
}