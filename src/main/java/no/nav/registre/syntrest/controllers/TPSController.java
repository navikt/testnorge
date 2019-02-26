/*
package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.TPSService;
import no.nav.registre.syntrest.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
public class TPSController extends KubernetesUtils {

    @Value("${max_retrys}")
    private int retryCount;

    @Value("${synth-tps-app}")
    private String appName;

    @Autowired
    private Validation validation;

    @Autowired
    private TPSService tpsService;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @GetMapping(value = "/tps/{endringskode}")
    public ResponseEntity generateTps(@PathVariable String endringskode, @RequestParam int numToGenerate) throws IOException, ApiException {
        if (!validation.validateEndringskode(endringskode)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Not a valid endringskode. Needs to be one of " + validation.getEndringskoder().toString());
        }
        counterLock.lock();
        counter++;
        counterLock.unlock();
        lock.lock();
        ApiClient client = createApiClient();
        try {
            createApplication(client, "/nais/synthdata-tps.yaml", tpsService);
            log.info("Requesting synthetic data: synthdata-tps");
            Object synData = getData(numToGenerate, endringskode);
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e) {
            log.info("Exception in generateTps: " + e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        } finally {
            counter--;
            System.out.println("Counter: " + counter);
            lock.unlock();
            if (counter == 0)
                deleteApplication(client, appName);
        }
    }

    public Object getData(int num_to_generate, String endringskode) throws InterruptedException {
        int attempt = 0;
        while (attempt < retryCount) {
            try {
                List<Map<String, Object>> synData = tpsService.generateTPSFromNAIS(num_to_generate, endringskode);
                return synData;
            } catch (Exception e) {
                TimeUnit.SECONDS.sleep(1);
                attempt++;
            }
        }
        return new Exception("Could not retrieve data in " + retryCount + " attempts. Aborting");
    }
}
*/
