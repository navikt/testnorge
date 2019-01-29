package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.globals.QueueHandler;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.PenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class PenController extends KubernetesUtils {

    @Value("${synth-pen-app}")
    private String appName;

    @Autowired
    private PenService penService;

    @Autowired
    private QueueHandler queue;

    @GetMapping(value = "/generatePen/{num_to_generate}")
    public ResponseEntity generatePen(@PathVariable int num_to_generate) throws IOException, ApiException {
        int queueId = queue.getQueueId();
        queue.addToQueue(queueId);
        //ApiClient client = createApiClient();
        KubeConfig kc = KubeConfig.loadKubeConfig(new FileReader("C:\\nais\\kubeconfigs\\config"));
        ApiClient client = Config.fromConfig(kc);
        try {
            createApplication(client, "/nais/synthdata-pen.yaml", penService);
            while (queue.getNextInQueue() != queueId) {
                TimeUnit.SECONDS.sleep(2);
            }
            log.info("Requesting synthetic data from: synthdata-pen");
            CompletableFuture<List<Map<String, String>>> result = penService.generatePenFromNAIS(num_to_generate);
            List<Map<String, String>> synData = result.get();

            queue.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e) {
            log.info("Exception in generatePen: " + e.getCause());
            queue.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }
}
