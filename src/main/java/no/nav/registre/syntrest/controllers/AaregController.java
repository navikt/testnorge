package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.globals.QueueHandler;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.AaregService;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class AaregController extends KubernetesUtils {

    @Value("${synth-aareg-app}")
    private String appName;

    @Autowired
    private AaregService aaregService;

    @Autowired
    private QueueHandler queue;

    @PostMapping(value = "/generateAareg")
    public ResponseEntity generateSykemeldinger(@RequestBody List<String> request) throws IOException, ApiException {
        int queueId = queue.getQueueId();
        queue.addToQueue(queueId);
        ApiClient client = createApiClient();
        try {
            createApplication(client, "/nais/synthdata-aareg.yaml", aaregService);
            while (queue.getNextInQueue() != queueId) {
                TimeUnit.SECONDS.sleep(2);
            }
            log.info("Requesting synthetic data: synthdata-aareg for id " + queueId);
            CompletableFuture<String> result = aaregService.generateAaregFromNAIS(request);
            String synData = result.get();
            queue.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e) {
            log.info("Exception in generateAareg: " + e.getCause());
            queue.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }

    }
}