package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.globals.QueueHandler;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.TPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
public class TPController extends KubernetesUtils {

    @Value("${synth-tp-app}")
    private String appName;

    @Autowired
    private TPService tpService;

    private QueueHandler queueHandler = QueueHandler.getInstance();

    @GetMapping(value = "/generateTp/{num_to_generate}")
    public ResponseEntity generateTp(@PathVariable int num_to_generate) throws IOException, ApiException {
        int queueId = queueHandler.getQueueId();
        queueHandler.addToQueue(queueId);
        ApiClient client = createApiClient();
        try {
            System.out.println("Creating application: synthdata-tp");
            createApplication(client, "/nais/synthdata-tp.yaml", tpService);

            System.out.println("Requesting synthetic data: synthdata-tp");
            CompletableFuture<List<Map<String, String>>> result = tpService.generateTPFromNAIS(num_to_generate);
            List<Map<String, String>> synData = result.get();

            queueHandler.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e) {
            log.info("Exception in generateTp: " + e);
            queueHandler.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }

    }
}
