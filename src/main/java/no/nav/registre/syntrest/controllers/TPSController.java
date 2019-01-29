package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.globals.QueueHandler;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.TPSService;
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
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class TPSController extends KubernetesUtils {

    @Value("${synth-tps-app}")
    private String appName;

    @Autowired
    private TPSService tpsService;

    @Autowired
    private QueueHandler queue;

    @GetMapping(value = "/generateTps/{num_to_generate}/{endringskode}")
    public ResponseEntity generateTps(@PathVariable int num_to_generate, @PathVariable String endringskode) throws IOException, ApiException {
        int queueId = queue.getQueueId();
        queue.addToQueue(queueId);
        ApiClient client = createApiClient();
        try {
            createApplication(client, "/nais/synthdata-tps.yaml", tpsService);
            while (queue.getNextInQueue() != queueId) {
                TimeUnit.SECONDS.sleep(2);
            }
            log.info("Requesting synthetic data: synthdata-tps for id " + queueId);
            CompletableFuture<List<Map<String, Object>>> result = tpsService.generateTPSFromNAIS(num_to_generate, endringskode);
            List<Map<String, Object>> synData = result.get();

            queue.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e) {
            log.info("Exception in generateTps: " + e.getCause());
            queue.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }
    }
}
