package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
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
        log.info("Creating application: synthdata-eia");
        createApplication(client, "/nais/synthdata-eia.yaml", eiaService);

        log.info("Requesting synthetic data from: synthdata-eia");
        CompletableFuture<List<String>> result = eiaService.generateSykemeldingerFromNAIS(request);
        List<String> synData = result.get();

        queueHandler.removeFromQueue(queueId, client, appName);
        return ResponseEntity.status(HttpStatus.OK).body(synData);
    }
}
