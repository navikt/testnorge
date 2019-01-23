package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import no.nav.registre.syntrest.globals.QueueHandler;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.MedlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("api/v1")
public class MedlController extends KubernetesUtils {

    @Value("${synth-medl-app}")
    private String appName;

    @Autowired
    private MedlService medlService;

    private QueueHandler queueHandler = QueueHandler.getInstance();

    @GetMapping(value = "/generateMedl/{num_to_generate}")
    public ResponseEntity generateMedl(@PathVariable int num_to_generate) throws InterruptedException, ExecutionException, IOException, ApiException {

        int queueId = queueHandler.getQueueId();
        queueHandler.addToQueue(queueId);
        ApiClient client = createApiClient();
        System.out.println("Creating application..");
        createApplication(client, "C:\\Users\\E154653\\Desktop\\synt\\syntrest\\src\\main\\java\\no\\nav\\registre\\syntrest\\config\\synthdata-medl.yaml", medlService);

        System.out.println("Requesting synthetic data..");
        CompletableFuture<List<Map<String, String>>> result = medlService.generateMedlFromNAIS(num_to_generate);
        List<Map<String, String>> synData = result.get();

        queueHandler.removeFromQueue(queueId, client, appName);
        return ResponseEntity.status(HttpStatus.OK).body(synData);
    }
}
