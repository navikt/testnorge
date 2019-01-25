package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.globals.QueueHandler;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.PoppService;
import no.nav.registre.syntrest.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class PoppController extends KubernetesUtils {

    @Value("${synth-popp-app}")
    private String appName;

    @Autowired
    private Validation validation;

    @Autowired
    private PoppService poppService;

    @Autowired
    private QueueHandler queue;

    @PostMapping(value = "/generatePopp")
    public ResponseEntity generatePopp(@RequestBody String[] fnrs) throws IOException, ApiException {
        if (!validation.validateFnrs(fnrs)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Fødselsnummer needs to be of type String and length 11.");
        }
        int queueId = queue.getQueueId();
        queue.addToQueue(queueId);
        ApiClient client = createApiClient();
        try {
            createApplication(client, "/nais/synthdata-popp.yaml", poppService);
            while (queue.getNextInQueue() != queueId) {
                TimeUnit.SECONDS.sleep(2);
            }
            log.info("Requesting synthetic data: synthdata-popp for id " + queueId);
            Future<List<Map<String, Object>>> completableFuture = poppService.generatePoppMeldingerFromNAIS(fnrs);
            List<Map<String, Object>> synData = completableFuture.get();
            queue.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e) {
            System.out.println(e);
            log.info("Exception in generatePopp: " + e.getCause());
            queue.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }

    }
}
