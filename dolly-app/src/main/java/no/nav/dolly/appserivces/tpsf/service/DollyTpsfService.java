package no.nav.dolly.appserivces.tpsf.service;

import no.nav.dolly.appserivces.sigrunstub.restcom.SigrunStubApiService;
import no.nav.dolly.appserivces.tpsf.restcom.TpsfApiService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.IdentService;
import no.nav.dolly.service.TestgruppeService;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import static no.nav.dolly.util.UtilFunctions.isNullOrEmpty;

//TODO Denne må refaktoreres til å bli mer ryddig.
@Service
public class DollyTpsfService {

    @Autowired
    TpsfApiService tpsfApiService;

    @Autowired
    TestgruppeService testgruppeService;

    @Autowired
    IdentService identService;

    @Autowired
    SigrunStubApiService sigrunStubApiService;

    @Autowired
    IdentRepository identRepository;

    @Autowired
    BestillingProgressRepository bestillingProgressRepository;

    @Autowired
    BestillingService bestillingService;

    @Async
    public void opprettPersonerByKriterierAsync(Long gruppeId, RsDollyBestillingsRequest request, Long bestillingsId) {
        Bestilling bestilling = bestillingService.fetchBestillingById(bestillingsId);
        request.getTpsf().setEnvironments(request.getEnvironments());

        try {
            request.getTpsf().setAntall(1);
            for (int i = 0; i < request.getAntall(); i++) {
                List<String> bestilteIdenter = tpsfApiService.opprettIdenterTpsf(request.getTpsf());
                Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);

                String hovedPersonIdent = getHovedpersonAvBestillingsidenter(bestilteIdenter);
                BestillingProgress progress = new BestillingProgress(bestillingsId, hovedPersonIdent);
                senderIdenterTilTPS(request, bestilteIdenter, testgruppe, bestilling, progress);
            }

        } catch (Exception e) {
            //TODO Dette skal logges.
            System.out.println("##### LAGE IDENTER FEILET ######  Error: " + e.getMessage() +  "   feil: " + e.getCause() + "       error: " + e);
        } finally {
            bestilling.setFerdig(true);
            bestillingService.saveBestillingToDB(bestilling);
        }
    }

    private void senderIdenterTilTPS(RsDollyBestillingsRequest request,  List<String> klareIdenter, Testgruppe testgruppe, Bestilling bestilling, BestillingProgress progress) {
        try {
            RsSkdMeldingResponse response = tpsfApiService.sendTilTpsFraTPSF(klareIdenter, request.getEnvironments().stream().map(String::toLowerCase).collect(Collectors.toList()));
            String env = extractSuccessEnvTPS(response.getSendSkdMeldingTilTpsResponsene().get(0));

            if (!isNullOrEmpty(env)) {
                identService.saveIdentTilGruppe(getHovedpersonAvBestillingsidenter(klareIdenter), testgruppe);
                progress.setTpsfSuccessEnv(env);
            }
        } catch (TpsfException e) {
            progress.setFeil(e.getMessage());
        }

        bestillingProgressRepository.save(progress);
        bestillingService.saveBestillingToDB(bestilling);
    }

    private String getHovedpersonAvBestillingsidenter(List<String> identer){
        return identer.get(0); //TODO Rask fix for å hente hoveperson i bestilling. Vet at den er første, men burde gjøre en sikrere sjekk
    }

    private String extractSuccessEnvTPS(SendSkdMeldingTilTpsResponse response) {
        Map<String, String> status = response.getStatus();
        StringBuilder sb = new StringBuilder();

        for (Map.Entry<String, String> entry : status.entrySet()) {
            if (entry.getValue().contains("00")) {
                sb.append(entry.getKey()).append(",");
            }
        }

        String env = sb.toString();
        if (env.length() > 0) {
            env = env.substring(0, env.length() - 1);
        }

        return env;
    }
}
