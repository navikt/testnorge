package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.PoppService;
import no.nav.registre.syntrest.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/v1")
public class PoppController extends KubernetesUtils {

    @Autowired
    private Validation validation;

    @Autowired
    private PoppService poppService;

    @PostMapping(value = "/generatePopp")
    public ResponseEntity generatePopp(@RequestBody String[] fnrs) throws InterruptedException, ExecutionException, IOException, ApiException {
        if (!validation.validateFnrs(fnrs)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Fødselsnummer needs to be of type String and length 11.");
        }

        ApiClient client = createApiClient();

        System.out.println("Creating application..");
        createApplication(client, "src/main/java/no/nav/registre/syntrest/config/synthdata-popp.yaml");

        System.out.println("Checking liveness..");
        boolean stillDeploying = true;
        while (stillDeploying){
            try{
                if (poppService.isAlive().equals("1")){
                    System.out.println("It's Alive!");
                    stillDeploying = false;
                }
            } catch (HttpClientErrorException | HttpServerErrorException e){
                TimeUnit.SECONDS.sleep(1);
            }
        }

        System.out.println("Requesting synthetic data..");
        CompletableFuture<List<Map<String, Object>>> result = poppService.generatePoppMeldingerFromNAIS(fnrs);
        List<Map<String, Object>> synData = result.get();

        System.out.println("Deleting application..");
        deleteApplication(client, "synthdata-popp");

        return ResponseEntity.status(HttpStatus.OK).body(synData);
    }
}
