package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import no.nav.registre.syntrest.globals.QueueHandler;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.EIAService;
import no.nav.registre.syntrest.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${synth-eia-app}")
    private String appName;

    @Autowired
    private Validation validation;

    @Autowired
    private EIAService eiaService;

    private QueueHandler queueHandler = QueueHandler.getInstance();

    @PostMapping(value = "/generateSykemeldinger")
    public ResponseEntity generateSykemeldinger(@RequestBody List<Map<String, String>> request) throws InterruptedException, ExecutionException, IOException, ApiException {
        if (validation.validateEia(request) != true){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Fødselsnummer needs to be of type String and length 11.");
        }

        int queueId = queueHandler.getQueueId();
        queueHandler.addToQueue(queueId);
        ApiClient client = createApiClient();
        System.out.println("Creating application..");
        createApplication(client, "src/main/java/no/nav/registre/syntrest/config/synthdata-eia.yaml", eiaService);

        System.out.println("Requesting synthetic data..");
        CompletableFuture<List<String>> result = eiaService.generateSykemeldingerFromNAIS(request);
        List<String> synData = result.get();

        queueHandler.removeFromQueue(queueId, client, appName);

        return ResponseEntity.status(HttpStatus.OK).body(synData);
    }
}
