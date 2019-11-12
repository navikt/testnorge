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

    @Autowired
    private RestTemplate restTemplate;

    private final ApplicationManager applicationManager;
    private final String appName;

    public SyntConsumer(ApplicationManager applicationManager, String name) {
        this.applicationManager = applicationManager;
        this.appName = name;
    }

    public synchronized Object synthesizeData(RequestEntity request) throws ResponseStatusException {

        try {
            applicationManager.startApplication(this);
        } catch (ApiException | InterruptedException e) {
            log.error("Could not start synth package {}.", this.appName);
            return new ResponseEntity<>("Something went wrong when trying to deploy the synth pacakge.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return getDataFromSyntPackage(request);
    }

    private synchronized Object getDataFromSyntPackage(RequestEntity request) throws RestClientException {
        try {
            ResponseEntity response = restTemplate.exchange(request, Object.class);

            if (response.getStatusCode() != HttpStatus.OK) {
            log.warn("Unexpected synth response: {}\n{}", response.getStatusCode(), response.getBody());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Unexpected synth response: %s\n%s",
                            response.getStatusCode().toString(),
                            response.getBody().toString()));
            }
            return response.getBody();
        } catch (RestClientException e) {
            log.error("Unexpected Rest Client Exception: {}", Arrays.toString(e.getStackTrace()));
            shutdownApplication();
            e.printStackTrace();
            throw e;
        }
    }

    public synchronized void shutdownApplication() {
        applicationManager.shutdownApplication(appName);
    }

    public String getAppName() {
        return this.appName;
    }
}
