package no.nav.registre.syntrest.consumer;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Slf4j
public class SyntConsumer {
    /**
     * The SyntConsumer class talks to the synt-package once it has been deployed to the cluster.
     */

    private final ApplicationManager applicationManager;
    private final String appName;
    private final boolean shutdown;
    private final WebClient webClient;

    @Value("${synth-package-unused-uptime}")
    private long shutdownTimeDelaySeconds;

    public SyntConsumer(ApplicationManager applicationManager, String name, boolean shutdown) {
        this.applicationManager = applicationManager;
        this.appName = name;
        this.shutdown = shutdown;
    }

    public synchronized Object synthesizeData(RequestEntity request) throws ResponseStatusException, InterruptedException, ApiException {

        try {
            applicationManager.startApplication(this);
        } catch (ApiException | InterruptedException e) {
            log.error("Could access synth package {}: {}", this.appName, e.getMessage());
            throw e;
        }

        return getDataFromSyntPackage(request);
    }

    private synchronized Object getDataFromSyntPackage(RequestEntity request) throws RestClientException {
        try {
            ResponseEntity response = restTemplate.exchange(request, Object.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                log.warn("Unexpected synth response: {}", response.getStatusCode());
                throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                        String.format("Unexpected synth response: %s", response.getStatusCode().toString()));
            }
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Unexpected Rest Client Exception: {}", Arrays.toString(e.getStackTrace()));
            throw e;
        } finally {
            if (this.shutdown) {
                scheduleShutdown(shutdownTimeDelaySeconds);
            }
        }
    }

    synchronized void scheduleShutdown(long shutdownTimeDelaySeconds) {
        applicationManager.scheduleShutdown(this, shutdownTimeDelaySeconds);
    }

    public synchronized void shutdownApplication() {
        applicationManager.shutdownApplication(appName);
    }

    public String getAppName() {
        return this.appName;
    }
}
