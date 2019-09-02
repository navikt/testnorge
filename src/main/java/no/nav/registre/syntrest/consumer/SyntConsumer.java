package no.nav.registre.syntrest.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
/**
 * Ensures that only one call is done to each SyntPackage at a time. (If multiple calls is done while not returned,
 * they will break.)
 */
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
    private AtomicInteger numClients;

    public SyntConsumer(ApplicationManager applicationManager, RestTemplate restTemplate) {
        this.applicationManager = applicationManager;
        this.restTemplate = restTemplate;
        this.numClients = new AtomicInteger(0);
    }

    public T synthesizeData(String appName, RequestEntity request) {
        this.numClients.incrementAndGet();
        applicationManager.startApplication(appName);

        T synthesizedData = accessSyntPackage(request);
        this.numClients.decrementAndGet();

        if (this.numClients.get() <= 0) {
            applicationManager.shutdownApplication(appName);
        }

        // TODO: bug
        // The call to rest template should take so long that any concurrent threads will be able to
        // increase numClients before it exits.
        // However, if two methods comes *right after each other* the application will be spun down,
        // just to be spun up again right after..

        return synthesizedData;
    }

    private synchronized T accessSyntPackage(RequestEntity request) {
        ResponseEntity<T> response = restTemplate.exchange(request, RESPONSE_TYPE);
        return response.getBody();
    }
}
