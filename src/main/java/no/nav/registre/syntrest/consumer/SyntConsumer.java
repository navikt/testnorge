package no.nav.registre.syntrest.consumer;

import io.kubernetes.client.ApiException;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;

import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.UriTemplate;

import java.util.Arrays;

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

    public Object generateForCodeAndNumber(String url, String code, int numToGenerate) throws ResponseStatusException {
        UriTemplate uri = new UriTemplate(url);
        return synthesizeData(RequestEntity.get(uri.expand(numToGenerate, code)).build());
    }

    public Object generateForNumbers(String url, int numToGenerate) throws ResponseStatusException {
        UriTemplate uri = new UriTemplate(url);
        return synthesizeData(RequestEntity.get(uri.expand(numToGenerate)).build());
    }

    public Object synthesizeDataPostRequest(String url, Object body) throws ResponseStatusException {
        UriTemplate uri = new UriTemplate(url);
        return synthesizeData(RequestEntity.post(uri.expand()).body(body));
    }

    private Object synthesizeData(RequestEntity request) throws ResponseStatusException {

        try {
            applicationManager.startApplication(this);
        } catch (ApiException | InterruptedException e) {
            log.error("Could not start synth package {}.", this.appName);
            return new ResponseEntity<>("Something went wrong when trying to deploy the synth pacakge.", HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return getDataFromSyntPackage(request);
    }

    // In the syntConsumer because we will allow synt packages of other types to be accessed asynchronously,
    // but calls to the *same SyntPackage* should happen one at a time.
    private synchronized Object getDataFromSyntPackage(RequestEntity request) throws ResponseStatusException {
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
            log.error("Unexpected client-side error: {}", Arrays.toString(e.getStackTrace()));
            shutdownApplication();
            e.printStackTrace();
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    String.format("Unexpected client side error: \n%s",
                            Arrays.toString(e.getStackTrace())));
        }
    }

    public void shutdownApplication() {
        applicationManager.shutdownApplication(appName);
    }

    public String getAppName() {
        return this.appName;
    }
}
