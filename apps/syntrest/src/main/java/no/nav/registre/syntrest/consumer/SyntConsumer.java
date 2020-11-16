package no.nav.registre.syntrest.consumer;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.util.Arrays;

@Slf4j
public class SyntConsumer {

    final ApplicationManager applicationManager;
    final String appName;
    @Autowired
    private RestTemplate restTemplate;

    public SyntConsumer(ApplicationManager applicationManager, String name) {
        this.applicationManager = applicationManager;
        this.appName = name;
    }

    public synchronized Object synthesizeData(RequestEntity request) throws ResponseStatusException {

        try {
            log.info("Starting synth package {}...", this.appName);
            applicationManager.startApplication(this);
        } catch (ApiException | InterruptedException e) {
            log.error("Could not start synth package {}: {}", this.appName, e.getMessage());
            return null;
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
            scheduleShutdown();
        }
    }

    synchronized void scheduleShutdown() {
        applicationManager.scheduleShutdown(this);
    }

    public synchronized void shutdownApplication() {
        applicationManager.shutdownApplication(appName);
    }

    public String getAppName() {
        return this.appName;
    }
}
