package no.nav.registre.syntrest.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

@Slf4j
public class SyntConsumer {

    private final ApplicationManager applicationManager;
    private final RestTemplate restTemplate;
    private final String appName;

    public SyntConsumer(ApplicationManager applicationManager, RestTemplate restTemplate, String name) {
        this.applicationManager = applicationManager;
        this.restTemplate = restTemplate;
        this.appName = name;
    }

    public Object generateForCodeAndNumber(String url, String code, int numToGenerate) {
        UriTemplate uri = new UriTemplate(url);
        return synthesizeData(RequestEntity.get(uri.expand(numToGenerate, code)).build());
    }

    public Object generateForNumbers(String url, int numToGenerate) {
        UriTemplate uri = new UriTemplate(url);
        return synthesizeData(RequestEntity.get(uri.expand(numToGenerate)).build());
    }

    public Object synthesizeDataPostRequest(String url, Object body) {
        UriTemplate uri = new UriTemplate(url);
        return synthesizeData(RequestEntity.post(uri.expand()).body(body));
    }

    private Object synthesizeData(RequestEntity request) {

        if (applicationManager.startApplication(this) == -1) {
            log.error("Could not start synth package {}", this.appName);
            return new ResponseEntity<>("Something went wrong when trying to deploy the synth pacakge.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return accessSyntPackage(request);
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

    public String getAppName() {
        return this.appName;
    }
}
