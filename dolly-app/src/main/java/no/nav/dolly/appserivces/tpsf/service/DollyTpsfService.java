package no.nav.dolly.appserivces.tpsf.service;

import no.nav.dolly.appserivces.sigrunstub.restcom.SigrunStubApiService;
import no.nav.dolly.appserivces.tpsf.restcom.TpsfApiService;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultset.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultset.RsGrunnlagResponse;
import no.nav.dolly.domain.resultset.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultset.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.exceptions.TpsfException;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.TestgruppeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

//TODO Denne må refaktoreres til å bli mer ryddig.
@Service
public class DollyTpsfService {

    @Autowired
    TpsfApiService tpsfApiService;

    @Autowired
    TestgruppeService testgruppeService;

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
        request.getTpsf().setAntall(request.getAntall());
        request.getTpsf().setEnvironments(request.getEnvironments());

        try {
            for (int i = 0; i < request.getAntall(); i++) {
                RsDollyBestillingsRequest tempRequest = request.copy();
                tempRequest.setAntall(1);

                List<String> klareIdenter = tpsfApiService.opprettPersonerTpsf(tempRequest.getTpsf());
                Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);

                createIdenterIRegistrene(request, bestilling, klareIdenter, testgruppe);
            }

        } catch (Exception e) {
            // LOG Exception eller set i bestilling som error. Er Async, så skal ikke håndtere error.
            //TODO Dette skal logges.
            System.out.println("##### LAGE IDENTER FEILET ######  Error: " + e.getMessage() +  "   feil: " + e.getCause() + "       error: " + e);
        } finally {
            bestilling.setSistOppdatert(LocalDateTime.now());
            bestilling.setFerdig(true);
            bestillingService.saveBestillingToDB(bestilling);
        }
    }

    private void createIdenterIRegistrene(RsDollyBestillingsRequest request, Bestilling bestilling, List<String> klareIdenter, Testgruppe testgruppe) {
        String hovedPersonIdent = klareIdenter.get(0);   //TODO Rask fix for å hente hoveperson i bestilling. Vet at den er første, men burde gjøre en sikrere sjekk

        BestillingProgress progress = new BestillingProgress();
        progress.setBestillingId(bestilling.getId());
        progress.setIdent(hovedPersonIdent);

        RsSkdMeldingResponse response;
        try {
            response = tpsfApiService.sendTilTpsFraTPSF(klareIdenter, request.getEnvironments().stream().map(String::toLowerCase).collect(Collectors.toList()));
        } catch (TpsfException e) {
            progress.setFeil(e.getMessage());
            bestillingProgressRepository.save(progress);
            bestilling.setSistOppdatert(LocalDateTime.now());
            bestillingService.saveBestillingToDB(bestilling);
            return;
        }

        String env = extractSuccessEnvTPS(response.getSendSkdMeldingTilTpsResponsene().get(0));

        BestillingProgress currentProgress = bestillingProgressRepository.save(progress);

        bestilling.setSistOppdatert(LocalDateTime.now());
        bestillingService.saveBestillingToDB(bestilling);

        if (env.length() > 0) {
            Testident testident = new Testident();
            testident.setIdent(hovedPersonIdent);
            testident.setTestgruppe(testgruppe);
            identRepository.save(testident);
        }

        /* Sigrunn Handlinger */
        if (request.getSigrunRequest() != null) {
            try {
                List<RsGrunnlagResponse> res = sigrunStubApiService.createInntektstuff(request.getSigrunRequest());
                currentProgress.setSigrunSuccessEnv("all");
            } catch (Exception e) {
                currentProgress.setFeil(e.getMessage());
            }

            currentProgress = bestillingProgressRepository.save(progress);
        }
    }

    private String identTilString(List<String> identer){
        StringBuilder sb = new StringBuilder();
        identer.forEach(i -> sb.append(i).append(","));
        return sb.toString();
    }

    private String extractSuccessEnvTPS(SendSkdMeldingTilTpsResponse response) {
        Map<String, String> status = response.getStatus();
        StringBuilder sb = new StringBuilder();
        //        String env = "";

        for (Map.Entry<String, String> entry : status.entrySet()) {
            if (entry.getValue().contains("00")) {
                sb.append(entry.getKey()).append(",");
                //                env += entry.getKey() + ",";
            }
        }

        String env = sb.toString();
        if (env.length() > 0) {
            env = env.substring(0, env.length() - 1);
        }

        return env;
    }
}
