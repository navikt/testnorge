package no.nav.appserivces.tpsf.service;

import no.nav.appserivces.sigrunstub.restcom.SigrunStubApiService;
import no.nav.appserivces.tpsf.restcom.TpsfApiService;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.exceptions.DollyFunctionalException;
import no.nav.jpa.Bestilling;
import no.nav.jpa.BestillingProgress;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;
import no.nav.resultSet.RsDollyBestillingsRequest;
import no.nav.resultSet.RsGrunnlagResponse;
import no.nav.resultSet.RsSkdMeldingResponse;
import no.nav.resultSet.SendSkdMeldingTilTpsResponse;
import no.nav.service.BestillingService;
import no.nav.service.TestgruppeService;

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
    public void opprettPersonerByKriterier(Long gruppeId, RsDollyBestillingsRequest request, Long bestillingsId){
        List<String> klareIdenter = tpsfApiService.opprettPersonerTpsf(request);
        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);

        Bestilling bestilling = bestillingService.fetchBestillingById(bestillingsId);

        klareIdenter.forEach(ident -> {
            BestillingProgress progress = new BestillingProgress();
            progress.setBestillingsId(bestillingsId);
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
            progress.setTpsfSuccessEnv(env.substring(0, env.length() - 1));

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

        bestilling.setSistOppdatert(LocalDateTime.now());
        bestilling.setFerdig(true);
        bestillingService.saveBestillingToDB(bestilling);
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
