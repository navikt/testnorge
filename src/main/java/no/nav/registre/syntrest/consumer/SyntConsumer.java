package no.nav.registre.syntrest.consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import no.nav.registre.syntrest.utils.SyntAppNames;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Getter
public class SyntConsumer {

    private final ApplicationManager applicationManager;
    private final RestTemplate restTemplate;
    private final String appName;

    public SyntConsumer(ApplicationManager applicationManager, RestTemplate restTemplate, SyntAppNames name) {
        this.applicationManager = applicationManager;
        this.restTemplate = restTemplate;
        this.appName = name.getName();
    }

    public Object synthesizeData(RequestEntity request) {

        if (!applicationManager.applicationIsAlive(appName)) {
            int started = applicationManager.startApplication(this);
            if (started == -1) {
                log.error("Could not start synth package {}", this.appName);
                return new ResponseEntity<>("Something went wrong when trying to deploy the synth pacakge.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        Object synthesizedData = accessSyntPackage(request);
        applicationManager.updateAccessedPackages(this);

        return synthesizedData;
    }

    public void shutdownApplication() {
        applicationManager.shutdownApplication(appName);
    }

    // In the syntConsumer because we will allow synt packages of other types to be accessed asynchronously,
    // but calls to the *same SyntPackage* should happen one at a time.
    private synchronized Object accessSyntPackage(RequestEntity request) {
        ResponseEntity response = restTemplate.exchange(request, Object.class);
        return response.getBody();
    }
}
