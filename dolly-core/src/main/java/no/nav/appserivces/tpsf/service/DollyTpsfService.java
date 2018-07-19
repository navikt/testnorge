package no.nav.appserivces.tpsf.service;

import no.nav.appserivces.tpsf.domain.request.RsDollyBestillingsRequest;
import no.nav.appserivces.tpsf.domain.response.RsSkdMeldingResponse;
import no.nav.appserivces.tpsf.domain.response.SendSkdMeldingTilTpsResponse;
import no.nav.appserivces.tpsf.restcom.TpsfApiService;
import no.nav.dolly.repository.BestillingProgressRepository;
import no.nav.dolly.repository.IdentRepository;
import no.nav.exceptions.DollyFunctionalException;
import no.nav.jpa.BestillingProgress;
import no.nav.jpa.Testgruppe;
import no.nav.jpa.Testident;
import no.nav.service.TestgruppeService;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class DollyTpsfService {

    @Autowired
    TpsfApiService tpsfApiService;

    @Autowired
    TestgruppeService testgruppeService;

    @Autowired
    IdentRepository identRepository;

    @Autowired
    BestillingProgressRepository bestillingProgressRepository;

    @Async
    public void opprettPersonerByKriterier(Long gruppeId, RsDollyBestillingsRequest request, Long bestillingsId){

        List<String> klareIdenter = tpsfApiService.opprettPersonerTpsf(request);

        Testgruppe testgruppe = testgruppeService.fetchTestgruppeById(gruppeId);

        klareIdenter.forEach(ident -> {
            BestillingProgress progress = new BestillingProgress();
            progress.setBestillingsId(bestillingsId);
            progress.setIdent(ident);

            RsSkdMeldingResponse response;
            try{
                response = tpsfApiService.sendTilTpsFraTPSF(ident, request.getEnvironments());
            } catch (DollyFunctionalException e){
                progress.setFeil(e.getMessage());
                bestillingProgressRepository.save(progress);
                return;
            }

            String env = extractSuccessEnvTPS(response.getSendSkdMeldingTilTpsResponsene().get(0));
            progress.setTpsfSuccessEnv(env);

            bestillingProgressRepository.save(progress);

            if(env.length() > 0){
                Testident testident = new Testident();
                testident.setIdent(ident);
                testident.setTestgruppe(testgruppe);
                identRepository.save(testident);
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
