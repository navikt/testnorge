package no.nav.dolly.bestilling.service;

import static java.lang.String.format;
import static java.util.Objects.nonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import no.nav.dolly.bestilling.krrstub.KrrStubResponseHandler;
import no.nav.dolly.bestilling.krrstub.KrrStubService;
import no.nav.dolly.bestilling.sigrunstub.SigrunStubResponseHandler;
import no.nav.dolly.bestilling.sigrunstub.SigrunStubService;
import no.nav.dolly.bestilling.tpsf.TpsfResponseHandler;
import no.nav.dolly.bestilling.tpsf.TpsfService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.domain.resultset.krrstub.DigitalKontaktdataRequest;
import no.nav.dolly.domain.resultset.sigrunstub.RsOpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TestgruppeService;

@Slf4j
@Service
public class DollyBestillingService {

    private static final String INNVANDRINGS_MLD_NAVN = "innvandringcreate";
    private static final String CACHE_GRUPPE = "gruppe";

    @Autowired
    private TpsfResponseHandler tpsfResponseHandler;


    @Autowired
    private TpsfService tpsfService;

    @Autowired
    private TestgruppeService testgruppeService;

    @Autowired
    private IdentService identService;

    @Autowired
    private SigrunStubService sigrunStubService;

    @Autowired
    private SigrunStubResponseHandler sigrunstubResponseHandler;

    @Autowired
    private KrrStubService krrStubService;

    @Autowired
    private KrrStubResponseHandler krrstubResponseHandler;

    @Autowired
    private BestillingProgressRepository bestillingProgressRepository;

    @Autowired
    private BestillingService bestillingService;

    @Autowired
    private MapperFacade mapperFacade;

    @Autowired
    private CacheManager cacheManager;

    @Async
    @Transactional
    public void opprettPersonerByKriterierAsync(Long gruppeId, RsDollyBestillingsRequest bestillingRequest, Long bestillingsId) {

        Bestilling bestilling = bestillingService.fetchBestillingById(bestillingsId);
        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);

        try {
            RsTpsfBestilling tpsfBestilling = nonNull(bestillingRequest.getTpsf()) ? bestillingRequest.getTpsf() : new RsTpsfBestilling();
            tpsfBestilling.setEnvironments(bestillingRequest.getEnvironments());
            tpsfBestilling.setAntall(1);

            int loopCount = 0;
            while (!bestillingService.isStoppet(bestillingsId) && loopCount < bestillingRequest.getAntall()) {
                List<String> bestilteIdenter = tpsfService.opprettIdenterTpsf(tpsfBestilling);
                String hovedPersonIdent = getHovedpersonAvBestillingsidenter(bestilteIdenter);
                BestillingProgress progress = new BestillingProgress(bestillingsId, hovedPersonIdent);

                sendIdenterTilTPS(bestillingRequest, bestilteIdenter, testgruppe, progress);

                handleSigrunstub(bestillingRequest, hovedPersonIdent, progress);

                handleKrrstub(bestillingRequest, bestillingsId, hovedPersonIdent, progress);

                if (!bestillingService.isStoppet(bestillingsId)) {
                    bestillingProgressRepository.save(progress);
                    bestilling.setSistOppdatert(LocalDateTime.now());
                    bestillingService.saveBestillingToDB(bestilling);
                }
                clearCache();
                loopCount++;
            }
        } catch (Exception e) {
            log.error("Bestilling med id <" + bestillingsId + "> til gruppeId <" + gruppeId + "> feilet grunnet " + e.getMessage(), e);
            bestilling.setFeil(format("FEIL: Bestilling kunne ikke utføres mot TPS: %s", e.getMessage()));
        } finally {
            if (bestillingService.isStoppet(bestillingsId)) {
                identService.slettTestidenter(bestilling.getId());
                bestilling.setStoppet(true);
            }
            bestilling.setFerdig(true);
            bestillingService.saveBestillingToDB(bestilling);
            clearCache();
        }
    }

    private void clearCache() {
        if (nonNull(cacheManager.getCache(CACHE_GRUPPE))) {
            cacheManager.getCache(CACHE_GRUPPE).clear();
        }
    }

    private void sendIdenterTilTPS(RsDollyBestillingsRequest request, List<String> klareIdenter, Testgruppe testgruppe, BestillingProgress progress) {
        try {
            RsSkdMeldingResponse response = tpsfService.sendIdenterTilTpsFraTPSF(klareIdenter, request.getEnvironments().stream().map(String::toLowerCase).collect(Collectors.toList()));
            String feedbackTps = tpsfResponseHandler.extractTPSFeedback(response.getSendSkdMeldingTilTpsResponsene());
            log.info(feedbackTps);

            String hovedperson = getHovedpersonAvBestillingsidenter(klareIdenter);
            List<String> successMiljoer = extraxtSuccessMiljoForHovedperson(hovedperson, response);
            List<String> failureMiljoer = extraxtFailureMiljoForHovedperson(hovedperson, response);

            if (!successMiljoer.isEmpty()) {
                identService.saveIdentTilGruppe(hovedperson, testgruppe);
                progress.setTpsfSuccessEnv(String.join(",", successMiljoer));
            }
            if (!failureMiljoer.isEmpty()) {
                progress.setFeil(String.join(",", failureMiljoer));
                log.warn("Person med ident: {} ble ikke opprettet i TPS", hovedperson);
            }
        } catch (TpsfException e) {
            tpsfResponseHandler.setErrorMessageToBestillingsProgress(e, progress);
        }

        bestillingProgressRepository.save(progress);
    }

    private void handleKrrstub(RsDollyBestillingsRequest bestillingRequest, Long bestillingsId, String hovedPersonIdent, BestillingProgress progress) {
        if (nonNull(bestillingRequest.getKrrstub())) {
            DigitalKontaktdataRequest digitalKontaktdataRequest = mapperFacade.map(bestillingRequest, DigitalKontaktdataRequest.class);
            digitalKontaktdataRequest.setPersonident(hovedPersonIdent);
            ResponseEntity krrstubResponse = krrStubService.createDigitalKontaktdata(bestillingsId, digitalKontaktdataRequest);
            progress.setKrrstubStatus(krrstubResponseHandler.extractResponse(krrstubResponse));
        }
    }

    private void handleSigrunstub(RsDollyBestillingsRequest bestillingRequest, String hovedPersonIdent, BestillingProgress progress) {
        if (nonNull(bestillingRequest.getSigrunstub())) {
            for (RsOpprettSkattegrunnlag request : bestillingRequest.getSigrunstub()) {
                request.setPersonidentifikator(hovedPersonIdent);
            }
            ResponseEntity sigrunResponse = sigrunStubService.createSkattegrunnlag(bestillingRequest.getSigrunstub());
            progress.setSigrunstubStatus(sigrunstubResponseHandler.extractResponse(sigrunResponse));
        }
    }

    private String getHovedpersonAvBestillingsidenter(List<String> identer) {
        return identer.get(0); //Rask fix for å hente hoveperson i bestilling. Vet at den er første, men burde gjøre en sikrere sjekk
    }

    private List<String> extraxtSuccessMiljoForHovedperson(String hovedperson, RsSkdMeldingResponse response) {
        List<String> successMiljoer = new ArrayList<>();

        for (SendSkdMeldingTilTpsResponse sendSkdMldResponse : response.getSendSkdMeldingTilTpsResponsene()) {

            if (isInnvandringsmeldingPaaPerson(hovedperson, sendSkdMldResponse)) {
                for (Map.Entry<String, String> entry : sendSkdMldResponse.getStatus().entrySet()) {
                    if ((entry.getValue().contains("OK"))) {
                        successMiljoer.add(entry.getKey());
                    }
                }
            }

        }

        return successMiljoer;
    }

    private List<String> extraxtFailureMiljoForHovedperson(String hovedperson, RsSkdMeldingResponse response) {
        List<String> failure = new ArrayList<>();

        for (SendSkdMeldingTilTpsResponse sendSkdMldResponse : response.getSendSkdMeldingTilTpsResponsene()) {

            if (isInnvandringsmeldingPaaPerson(hovedperson, sendSkdMldResponse)) {
                for (Map.Entry<String, String> entry : sendSkdMldResponse.getStatus().entrySet()) {
                    if (!(entry.getValue().contains("OK"))) {
                            failure.add(format("%s: %s", entry.getKey(), entry.getValue().replaceAll("^(08)(;08%)*", "FEIL: ").trim()));
                    }
                }
            }

        }

        return failure;
    }

    private boolean isInnvandringsmeldingPaaPerson(String personId, SendSkdMeldingTilTpsResponse r) {
        return r.getSkdmeldingstype() != null && INNVANDRINGS_MLD_NAVN.equalsIgnoreCase(r.getSkdmeldingstype()) && personId.equals(r.getPersonId());
    }
}