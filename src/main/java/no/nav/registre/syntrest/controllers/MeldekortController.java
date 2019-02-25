package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.MeldekortService;
import no.nav.registre.syntrest.utils.Validation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@RestController
@RequestMapping("api/v1/generate")
public class MeldekortController extends KubernetesUtils {

    @Value("${max_retrys}")
    private int retryCount;

    @Value("${synth-arena-meldekort-app}")
    private String appName;

    @Autowired
    private Validation validation;

    @Autowired
    private MeldekortService meldekortService;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @GetMapping(value = "/arena/meldekort/{meldegruppe}")
    public ResponseEntity generateMeldekort(@PathVariable String meldegruppe, @RequestParam int numToGenerate) throws ApiException, IOException {
        if (!validation.validateMeldegruppe(meldegruppe)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error: Not a valid meldegruppe. Needs to be one of: ATTF, DAGP, INDI, ARBS, FY");
        }
        counterLock.lock();
        counter++;
        counterLock.unlock();
        lock.lock();
        ApiClient client = createApiClient();
        try {
            createApplication(client, "/nais/synthdata-meldekort.yaml", meldekortService);
            log.info("Requesting synthetic data: synthdata-arena-meldekort");
            Object synData = getData(numToGenerate, meldegruppe);
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e) {
            log.info("Exception in generateMeldekort: " + e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        } finally {
            counter--;
            System.out.println("Counter: " + counter);
            lock.unlock();
            if (counter == 0)
                deleteApplication(client, appName);
        }
    }

    public Object getData(int num_to_generate, String meldegruppe) throws InterruptedException {
        int attempt = 0;
        while (attempt < retryCount) {
            try {
                CompletableFuture<List<String>> result = meldekortService.generateMeldekortFromNAIS(num_to_generate, meldegruppe);
                Object synData = result.get();
                return synData;
            } catch (Exception e) {
                TimeUnit.SECONDS.sleep(3);
                attempt++;
            }
        }
        return new Exception("Could not retrieve data in " + retryCount + " attempts. Aborting");
    }
}
