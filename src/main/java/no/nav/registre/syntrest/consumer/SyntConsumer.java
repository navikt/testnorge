package no.nav.registre.syntrest.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

@Slf4j
@Component
public class SyntConsumer<T> {
    //
    // // Hvis ikke, spinn opp ny via KubernetesController
    // Sjekk om den er ledig
    // Lås applikasjonen s.a. bare denne innstansen har tilgang
    // Gjør kall på applikasjonen
    // Lås opp applikasjonen igjen, og la andre få tilgang på den.
    // Sjekk om flere skal bruke applikasjonen
    // // Hvis ikke slett applikasjonen

    private final ParameterizedTypeReference<T> RESPONSE_TYPE = new ParameterizedTypeReference<T>() {
    };
    private final ApplicationManager applicationManager;
    private final RestTemplate restTemplate;
    private int numClients;

    public SyntConsumer(ApplicationManager applicationManager, RestTemplate restTemplate) {
        this.applicationManager = applicationManager;
        this.restTemplate = restTemplate;
        this.numClients = 0;
    }

    public T synthesizeData(String appName, RequestEntity request) {
        applicationManager.startApplication(appName);

        this.numClients++;
        T synthesizedData = accessSyntPackage(request); // SYNCHRONIZED CALL. CODE STOPS FOR EACH CLIENT?
        // this.numClients--;

        if (this.numClients <= 0) {
            applicationManager.shutdownApplication(appName);
        }

        return synthesizedData;
    }

    private synchronized T accessSyntPackage(RequestEntity request) {
        ResponseEntity<T> response = restTemplate.exchange(request, RESPONSE_TYPE);
        this.numClients--;

        return response.getBody();
    }
}
