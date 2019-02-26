/*
package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.BAService;
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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
public class BAController extends KubernetesUtils {

    @Value("${max_retrys}")
    private int retryCount;

    @Value("${synth-ba-app}")
    private String appName;

    @Autowired
    private Validation validation;

    @Autowired
    private BAService baService;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @PostMapping(value = "/ba")
    public ResponseEntity generateBa(@RequestBody String[] fnrs) throws ApiException, IOException {
        if (!validation.validateFnrs(fnrs)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Fødselsnummer needs to be of type String and length 11.");
        }
        counterLock.lock();
        counter++;
        counterLock.unlock();
        lock.lock();
        ApiClient client = createApiClient();
        try {
            createApplication(client, "/nais/synthdata-ba.yaml", baService);
            Object synData = getData(fnrs);
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e) {
            log.info("Exception in generateBa: " + e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        } finally {
            counter--;
            lock.unlock();
            if (counter == 0)
                deleteApplication(client, appName);
        }
    }

    public Object getData(String[] fnrs) throws InterruptedException {
        int attempt = 0;
        while (attempt < retryCount) {
            try {
                CompletableFuture<Object> result = baService.generateBaFromNAIS(fnrs);
                Object synData = result.get();
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
