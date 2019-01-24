package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.globals.QueueHandler;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.MedlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class MedlController extends KubernetesUtils {

    @Value("${synth-medl-app}")
    private String appName;

    @Autowired
    private MedlService medlService;

    @Autowired
    private QueueHandler queueHandler;

    @GetMapping(value = "/generateMedl/{num_to_generate}")
    public ResponseEntity generateMedl(@PathVariable int num_to_generate) throws IOException, ApiException {
        int queueId = queueHandler.getQueueId();
        queueHandler.addToQueue(queueId);
        ApiClient client = createApiClient();
        try {
            log.info("Creating application: synthdata-medl");
            createApplication(client, "/nais/synthdata-medl.yaml", medlService);

            log.info("Requesting synthetic data from: synthdata-medl");
            CompletableFuture<List<Map<String, String>>> result = medlService.generateMedlFromNAIS(num_to_generate);
            List<Map<String, String>> synData = result.get();

            queueHandler.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e) {
            log.info("Exception in generateMedl: " + e);
            queueHandler.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }

    }
}
