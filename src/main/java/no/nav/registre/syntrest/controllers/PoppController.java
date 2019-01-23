package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
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

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

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

    private QueueHandler queueHandler = QueueHandler.getInstance();

    @PostMapping(value = "/generatePopp")
    public ResponseEntity generatePopp(@RequestBody String[] fnrs) throws InterruptedException, ExecutionException, IOException, ApiException {
        if (!validation.validateFnrs(fnrs)){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Fødselsnummer needs to be of type String and length 11.");
        }

        int queueId = queueHandler.getQueueId();
        queueHandler.addToQueue(queueId);
        ApiClient client = createApiClient();
        log.info("Creating application: synthdata-popp");
        createApplication(client, "/nais/synthdata-popp.yaml", poppService);

        log.info("Requesting synthetic data: synthdata-popp");
        CompletableFuture<List<Map<String, Object>>> result = poppService.generatePoppMeldingerFromNAIS(fnrs);
        List<Map<String, Object>> synData = result.get();

        queueHandler.removeFromQueue(queueId, client, appName);
        return ResponseEntity.status(HttpStatus.OK).body(synData);
    }
}
