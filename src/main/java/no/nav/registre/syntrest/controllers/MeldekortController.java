package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.globals.QueueHandler;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.MeldekortService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class MeldekortController extends KubernetesUtils {

    @Value("${synth-arena-meldekort-app}")
    private String appName;

    @Autowired
    private MeldekortService meldekortService;

    @Autowired
    private QueueHandler queue;

    @GetMapping(value = "/generateMeldekort/{num_to_generate}/{meldegruppe}")
    public ResponseEntity generateMeldekort(@PathVariable int num_to_generate, @PathVariable String meldegruppe) throws ApiException, IOException {
        int queueId = queue.getQueueId();
        queue.addToQueue(queueId);
        ApiClient client = createApiClient();
        try {
            createApplication(client, "/nais/synthdata-meldekort.yaml", meldekortService);
            while (queue.getNextInQueue() != queueId) {
                TimeUnit.SECONDS.sleep(2);
            }
            log.info("Requesting synthetic data: synthdata-arena-meldekort for id " + queueId);
            CompletableFuture<List<String>> result = meldekortService.generateMeldekortFromNAIS(num_to_generate, meldegruppe);
            List<String> synData = result.get();

            queue.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e) {
            log.info("Exception in generateMeldekort: " + e.getCause());
            queue.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }

    }
}
