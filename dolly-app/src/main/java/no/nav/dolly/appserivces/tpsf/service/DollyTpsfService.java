package no.nav.dolly.appserivces.tpsf.service;

import no.nav.dolly.appserivces.sigrunstub.restcom.SigrunStubApiService;
import no.nav.dolly.appserivces.tpsf.restcom.TpsfApiService;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.domain.jpa.Bestilling;
import no.nav.dolly.domain.jpa.BestillingProgress;
import no.nav.dolly.domain.jpa.Testgruppe;
import no.nav.dolly.domain.jpa.Testident;
import no.nav.dolly.domain.resultSet.RsDollyBestillingsRequest;
import no.nav.dolly.domain.resultSet.RsGrunnlagResponse;
import no.nav.dolly.domain.resultSet.RsSkdMeldingResponse;
import no.nav.dolly.domain.resultSet.SendSkdMeldingTilTpsResponse;
import no.nav.dolly.service.BestillingService;
import no.nav.dolly.service.TestgruppeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

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
    public void opprettPersonerByKriterierAsync(Long gruppeId, RsDollyBestillingsRequest request, Long bestillingsId){
        List<String> klareIdenter = tpsfApiService.opprettPersonerTpsf(request);
        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);

        Bestilling bestilling = bestillingService.fetchBestillingById(bestillingsId);

        try {
            createIdenterIRegistrene(request, bestilling, klareIdenter, testgruppe);
        } catch (Exception e){
            // LOG Exception eller set i bestilling som error. Er Async, så skal ikke håndtere error.
        } finally {
            bestilling.setSistOppdatert(LocalDateTime.now());
            bestilling.setFerdig(true);
            bestillingService.saveBestillingToDB(bestilling);
        }
    }

    private void createIdenterIRegistrene(RsDollyBestillingsRequest request, Bestilling bestilling, List<String> klareIdenter, Testgruppe testgruppe){
        klareIdenter.forEach(ident -> {
            BestillingProgress progress = new BestillingProgress();
            progress.setBestillingId(bestilling.getId());
            progress.setIdent(ident);

            RsSkdMeldingResponse response;
            try{
                response = tpsfApiService.sendTilTpsFraTPSF(ident, request.getEnvironments().stream().map(String::toLowerCase).collect(Collectors.toList()));
            } catch (DollyFunctionalException e){
                progress.setFeil(e.getMessage());
                bestillingProgressRepository.save(progress);
                bestilling.setSistOppdatert(LocalDateTime.now());
                bestillingService.saveBestillingToDB(bestilling);
                return;
            }

            String env = extractSuccessEnvTPS(response.getSendSkdMeldingTilTpsResponsene().get(0));
            if(env.length() > 0){
                progress.setTpsfSuccessEnv(env.substring(0, env.length() - 1));
            }

            BestillingProgress currentProgress = bestillingProgressRepository.save(progress);

            bestilling.setSistOppdatert(LocalDateTime.now());
            bestillingService.saveBestillingToDB(bestilling);

            if(env.length() > 0){
                Testident testident = new Testident();
                testident.setIdent(ident);
                testident.setTestgruppe(testgruppe);
                identRepository.save(testident);
            }

            /* Sigrunn Handlinger */
            if(request.getSigrunRequest() != null) {
                try {
                    List<RsGrunnlagResponse> res = sigrunStubApiService.createInntektstuff(request.getSigrunRequest());
                    currentProgress.setSigrunSuccessEnv("all");
                }catch (Exception e){
                    currentProgress.setFeil(e.getMessage());
                }

                currentProgress = bestillingProgressRepository.save(progress);
            }
        });

    }

    private String extractSuccessEnvTPS(SendSkdMeldingTilTpsResponse response){
        Map<String, String> status = response.getStatus();
        String env = "";

        for(Map.Entry<String, String> entry : status.entrySet()){
            if(entry.getValue().contains("00")){
                env += entry.getKey() + ",";
            }
        }

        return env;
    }
}
