package no.nav.registre.syntrest.controllers;

import io.kubernetes.client.ApiClient;
import io.kubernetes.client.ApiException;
import io.kubernetes.client.util.Config;
import io.kubernetes.client.util.KubeConfig;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.KubernetesUtils;
import no.nav.registre.syntrest.services.IService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
@Component
public abstract class RootController extends KubernetesUtils {

    @Value("${max_retrys}")
    private int retryCount;

    protected ResponseEntity<? extends Object> generate(String appName, IService service, Object request, int counter, ReentrantLock lock, ReentrantLock counterLock) throws IOException, ApiException{
        counterLock.lock();
        counter++;
        counterLock.unlock();
        lock.lock();
        ApiClient client = createApiClient();
        //KubeConfig kc = KubeConfig.loadKubeConfig(new FileReader("C:\\nais\\kubeconfigs\\config"));
        //ApiClient client = Config.fromConfig(kc);
        try {
            createApplication(client, "/nais/" + appName + ".yaml", service);
            log.info("Requesting synthetic data: " + appName);
            Object synData = getData(request, service);
            System.out.println(synData);
            return ResponseEntity.status(HttpStatus.OK).body(synData);
        } catch (Exception e) {
            log.info("Exception while generating data for " + appName + ": " + e.getCause());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.toString());
        } finally {
            counter--;
            System.out.println("Counter: " + counter);
            lock.unlock();
            if (counter == 0)
                deleteApplication(client, appName);
        }
    }

    private Object getData(Object request, IService service) throws InterruptedException {
        int attempt = 0;
        while (attempt < retryCount) {
            try {
                Object synData = service.getDataFromNAIS(request);
                return synData;
            } catch (Exception e) {
                TimeUnit.SECONDS.sleep(1);
                attempt++;
            }
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Could not retrieve data in " + retryCount + " attempts. Aborting");
    }
}
