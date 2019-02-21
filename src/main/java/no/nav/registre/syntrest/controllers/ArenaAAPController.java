package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.ArenaAAPService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;


@Slf4j
@RestController
@RequestMapping("api/v1/generate")
public class ArenaAAPController extends KubernetesUtils {
    @Value("${max_retrys}")
    private int retryCount;

    @Value("${synth-arena-aap-app}")
    private String appName;

    @Autowired
    private ArenaAAPService aapService;

    private int counter = 0;

    ReentrantLock lock = new ReentrantLock();
    ReentrantLock counterLock = new ReentrantLock();

    @GetMapping(value = "/arena/aap")
    public ResponseEntity generateArenaAAP(@RequestParam int numToGenerate) throws IOException, ApiException {
        counterLock.lock();
        counter++;
        counterLock.unlock();
        lock.lock();
        ApiClient client = createApiClient();
        try {
            createApplication(client, "/nais/synthdata-arena-aap.yaml", aapService);
            log.info("Requesting synthetic data from: synthdata-arena-aap");
            Object synData = getData(numToGenerate);
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e) {
            log.info("Exception in generateArenaAAP: " + e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        } finally {
            counter--;
            System.out.println("Counter: " + counter);
            lock.unlock();
            if (counter == 0)
                deleteApplication(client, appName);
        }
    }

    public Object getData(int numToGenerate) throws InterruptedException {
        int attempt = 0;
        while (attempt < retryCount) {
            try {
                List<Map<String, String>> synData = aapService.generateArenaAAPFromNAIS(numToGenerate);
                return synData;
            } catch (Exception e) {
                TimeUnit.SECONDS.sleep(1);
                attempt++;
            }
        }
        return new Exception("Could not retrieve data in " + retryCount + " attempts. Aborting");
    }
}
