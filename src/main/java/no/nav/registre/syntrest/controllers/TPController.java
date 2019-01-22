package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.TPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/v1")
public class TPController extends KubernetesUtils {

    @Autowired
    private TPService tpService;

    @GetMapping(value = "/generateTp/{num_to_generate}")
    public ResponseEntity generateTp(@PathVariable int num_to_generate) throws InterruptedException, ExecutionException, IOException, ApiException {

        ApiClient client = createApiClient();

        System.out.println("Creating application..");
        createApplication(client, "src/main/java/no/nav/registre/syntrest/config/synthdata-tp.yaml");

        System.out.println("Checking liveness..");
        boolean stillDeploying = true;
        while (stillDeploying){
            try{
                if (tpService.isAlive().equals("1")){
                    System.out.println("It's Alive!");
                    stillDeploying = false;
                }
            } catch (HttpClientErrorException | HttpServerErrorException e){
                TimeUnit.SECONDS.sleep(1);
            }
        }

        System.out.println("Requesting synthetic data..");
        CompletableFuture<List<Map<String, String>>> result = tpService.generateTPFromNAIS(num_to_generate);
        List<Map<String, String>> synData = result.get();

        System.out.println("Deleting application..");
        deleteApplication(client, "synthdata-tp");

        return ResponseEntity.status(HttpStatus.OK).body(synData);
    }
}
