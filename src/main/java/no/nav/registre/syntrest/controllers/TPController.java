package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
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

import java.io.FileReader;
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

    @Autowired
    private int queue;

    @GetMapping(value = "/generateTp/{num_to_generate}")
    public ResponseEntity generateTp(@PathVariable int num_to_generate) throws IOException, ApiException {
        //ApiClient client = createApiClient();
        queue++;
        log.info("TP QueueSize: " + queue);
        KubeConfig kc = KubeConfig.loadKubeConfig(new FileReader("C:\\nais\\kubeconfigs\\config"));
        ApiClient client = Config.fromConfig(kc);
        try{

            createApplication(client, "/nais/synthdata-tp.yaml", tpService);

            log.info("Requesting synthetic data: synthdata-tp");
            CompletableFuture<List<Map<String, String>>> result = tpService.generateTPFromNAIS(num_to_generate);
            List<Map<String, String>> synData = result.get();
            queue--;
            log.info("TP QueueSize: " + queue);
            if (queue == 0){ deleteApplication(client, appName); }
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e){
            log.info("Something went horribly wrong in generateTp: " + e);
            queue--;
            log.info("TP QueueSize: " + queue);
            if (queue == 0){ deleteApplication(client, appName); }
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        }

    }
}
