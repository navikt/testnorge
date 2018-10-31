package no.nav.dolly.appservices.tpsf.service;

import static no.nav.dolly.util.UtilFunctions.isNullOrEmpty;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.appservices.sigrunstub.restcom.SigrunStubApiService;
import no.nav.dolly.appservices.tpsf.restcom.TpsfApiService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.RsSigrunnOpprettSkattegrunnlag;
import no.nav.dolly.domain.resultset.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.domain.resultset.tpsf.RsTpsfBestilling;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TestgruppeService;

@Slf4j
@Service
public class DollyTpsfService {

    private static final String INNVANDRINGS_MLD_NAVN = "innvandringcreate";

    @Autowired SigrunResponseHandler sigrunResponseHandler;

    @Autowired
    private TpsfResponseHandler tpsfResponseHandler;

    @Autowired
    private TpsfApiService tpsfApiService;

    @Autowired
    private TestgruppeService testgruppeService;

    @Autowired
    private IdentService identService;

    @Autowired
    private SigrunStubApiService sigrunStubApiService;

    @Autowired
    private BestillingProgressRepository bestillingProgressRepository;

    @Autowired
    private BestillingService bestillingService;

    @Async
    public void opprettPersonerByKriterierAsync(Long gruppeId, RsDollyBestillingsRequest bestillingsRequest, Long bestillingsId) {
        Bestilling bestilling = bestillingService.fetchBestillingById(bestillingsId);
        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);

        RsTpsfBestilling tpsfBestilling = bestillingsRequest.getTpsf();
        tpsfBestilling.setEnvironments(bestillingsRequest.getEnvironments());
        tpsfBestilling.setAntall(1);

        try {
            for (int i = 0; i < bestillingsRequest.getAntall(); i++) {
                List<String> bestilteIdenter = tpsfApiService.opprettIdenterTpsf(tpsfBestilling);
                String hovedPersonIdent = getHovedpersonAvBestillingsidenter(bestilteIdenter);
                BestillingProgress progress = new BestillingProgress(bestillingsId, hovedPersonIdent);

                senderIdenterTilTPS(bestillingsRequest, bestilteIdenter, testgruppe, progress);

                //Sigrun
                if (bestillingsRequest.getSigrunRequest() != null) {
                    for (RsSigrunnOpprettSkattegrunnlag request : bestillingsRequest.getSigrunRequest()) {
                        request.setPersonidentifikator(hovedPersonIdent);
                    }

                    ResponseEntity<String> sigrunResponse = sigrunStubApiService.createSkattegrunnlag(bestillingsRequest.getSigrunRequest());

                    String response = sigrunResponseHandler.extractResponse(sigrunResponse);
                    progress.setSigrunSuccessEnv(response);
                }

                bestillingProgressRepository.save(progress);

                bestillingService.saveBestillingToDB(bestilling);
            }
        } catch (Exception e) {
            log.error("Bestilling med id <" + bestillingsId + "> til gruppeId <" + gruppeId + "> feilet grunnet " + e.getMessage(), e);
        } finally {
            bestilling.setFerdig(true);
            bestillingService.saveBestillingToDB(bestilling);
        }
    }

    private void senderIdenterTilTPS(RsDollyBestillingsRequest request, List<String> klareIdenter, Testgruppe testgruppe, BestillingProgress progress) {
        try {
            RsSkdMeldingResponse response = tpsfApiService.sendIdenterTilTpsFraTPSF(klareIdenter, request.getEnvironments().stream().map(String::toLowerCase).collect(Collectors.toList()));
            String feedbackTps = tpsfResponseHandler.extractTPSFeedback(response.getSendSkdMeldingTilTpsResponsene());
            log.info(feedbackTps);

            String hovedperson = getHovedpersonAvBestillingsidenter(klareIdenter);
            List<String> successMiljoer = extraxtSuccessMiljoForHovedperson(hovedperson, response);

            if (!isNullOrEmpty(successMiljoer)) {
                identService.saveIdentTilGruppe(hovedperson, testgruppe);
                progress.setTpsfSuccessEnv(String.join(",", successMiljoer));
            } else {
                log.warn("Person med ident: {} ble ikke opprettet i TPS", hovedperson);
            }
        } catch (TpsfException e) {
            tpsfResponseHandler.setErrorMessageToBestillingsProgress(e, progress);
        }

        bestillingProgressRepository.save(progress);
    }

    private String getHovedpersonAvBestillingsidenter(List<String> identer) {
        return identer.get(0); //Rask fix for å hente hoveperson i bestilling. Vet at den er første, men burde gjøre en sikrere sjekk
    }

    private List<String> extraxtSuccessMiljoForHovedperson(String hovedperson, RsSkdMeldingResponse response) {
        List<String> successMiljoer = new ArrayList<>();

        for (SendSkdMeldingTilTpsResponse sendSkdMldResponse : response.getSendSkdMeldingTilTpsResponsene()) {

            if (isInnvandringsmeldingPaaPerson(hovedperson, sendSkdMldResponse)) {
                for (Map.Entry<String, String> entry : sendSkdMldResponse.getStatus().entrySet()) {
                    if ((entry.getValue().contains("00"))) {
                        successMiljoer.add(entry.getKey());
                    }
                }
            }

        }

        return successMiljoer;
    }

    private boolean isInnvandringsmeldingPaaPerson(String personId, SendSkdMeldingTilTpsResponse r) {
        return r.getSkdmeldingstype() != null && INNVANDRINGS_MLD_NAVN.equalsIgnoreCase(r.getSkdmeldingstype()) && personId.equals(r.getPersonId());
    }
}