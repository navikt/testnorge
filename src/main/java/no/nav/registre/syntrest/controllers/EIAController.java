package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.EIAService;
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
public class EIAController extends KubernetesUtils {

    @Autowired
    private Validation validation;

    @Autowired
    private EIAService eiaService;

    @PostMapping(value = "/generateSykemeldinger")
    public ResponseEntity generateSykemeldinger(@RequestBody List<Map<String, String>> request) throws InterruptedException, ExecutionException, IOException, ApiException {
        if (validation.validateEia(request) != true){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Fødselsnummer needs to be of type String and length 11.");
        }

        ApiClient client = createApiClient();

        System.out.println("Creating application..");
        createApplication(client, "src/main/java/no/nav/registre/syntrest/config/synthdata-eia.yaml");

        System.out.println("Checking liveness..");
        boolean stillDeploying = true;
        while (stillDeploying){
            try{
                if (eiaService.isAlive().equals("1")){
                    System.out.println("It's Alive!");
                    stillDeploying = false;
                }
            } catch (HttpClientErrorException | HttpServerErrorException e){
                TimeUnit.SECONDS.sleep(1);
            }
        }

        System.out.println("Requesting synthetic data..");
        CompletableFuture<List<String>> result = eiaService.generateSykemeldingerFromNAIS(request);
        List<String> synData = result.get();

        System.out.println("Deleting application..");
        deleteApplication(client, "synthdata-eia");

        return ResponseEntity.status(HttpStatus.OK).body(synData);
    }
}
