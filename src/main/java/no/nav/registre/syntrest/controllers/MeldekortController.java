package no.nav.registre.syntrest.controllers;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.EIAService;
import no.nav.registre.syntrest.services.MeldekortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class MeldekortController extends KubernetesUtils {

    @Autowired
    private MeldekortService meldekortService;

    @GetMapping(value = "/generateMeldekort/{num_to_generate}/{meldegruppe}")
    public ResponseEntity generateMeldekort(@PathVariable int num_to_generate, @PathVariable String meldegruppe) throws InterruptedException, ExecutionException, ApiException, IOException {

        ApiClient client = createApiClient();

        log.info("Creating application..");
        createApplication(client, "src/main/java/no/nav/registre/syntrest/config/synthdata-meldekort.yaml");

        log.info("Checking liveness..");
        boolean stillDeploying = true;
        while (stillDeploying){
            try{
                if (meldekortService.isAlive().equals("1")){
                    log.info("It's Alive!");
                    stillDeploying = false;
                }
            } catch (HttpClientErrorException | HttpServerErrorException e){
                TimeUnit.SECONDS.sleep(1);
            }
        }

        log.info("Requesting synthetic data..");
        CompletableFuture<List<String>> result = meldekortService.generateMeldekortFromNAIS(num_to_generate, meldegruppe);
        List<String> synData = result.get();

        log.info("Deleting application..");
        deleteApplication(client, "synthdata-meldekort");

        return ResponseEntity.status(HttpStatus.OK).body(synData);
    }
}
