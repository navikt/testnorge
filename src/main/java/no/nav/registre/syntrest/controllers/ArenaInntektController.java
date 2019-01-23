package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.domain.Inntektsmelding;
import no.nav.registre.syntrest.globals.QueueHandler;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.ArenaInntektService;
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

@Slf4j
@RestController
@RequestMapping("api/v1")
public class ArenaInntektController extends KubernetesUtils {

    @Value("${synth-arena-inntekt-app}")
    private String appName;

    @Autowired
    private Validation validation;

    @Autowired
    private ArenaInntektService arenaInntektService;

    private QueueHandler queueHandler = QueueHandler.getInstance();

    @PostMapping(value = "/generateArenaInntekt")
    public ResponseEntity generateInntektsmeldinger(@RequestBody String[] fnrs) throws ApiException, IOException {
        if (!validation.validateFnrs(fnrs)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Fødselsnummer needs to be of type String and length 11.");
        }
        int queueId = queueHandler.getQueueId();
        queueHandler.addToQueue(queueId);
        ApiClient client = createApiClient();
        try {
            log.info("Creating application: synthdata-arena-inntekt");
            createApplication(client, "/nais/synthdata-inntekt.yaml", arenaInntektService);

            log.info("Requesting synthetic data from: synthdata-arena-inntekt");
            CompletableFuture<Map<String, List<Inntektsmelding>>> result = arenaInntektService.generateInntektsmeldingerFromNAIS(fnrs);
            Map<String, List<Inntektsmelding>> synData = result.get();

            queueHandler.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e) {
            log.info("Exception in generateInntektsmeldinger: " + e);
            queueHandler.removeFromQueue(queueId, client, appName);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e);
        }
    }
}
