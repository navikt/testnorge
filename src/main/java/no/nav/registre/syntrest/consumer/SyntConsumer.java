package no.nav.registre.syntrest.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Component
/**
 * Ensures that only one call is done to each SyntPackage at a time. (If multiple calls is done while not returned,
 * they will break.)
 */
public class SyntConsumer {
    //
    // // Hvis ikke, spinn opp ny via KubernetesController
    // Sjekk om den er ledig
    // Lås applikasjonen s.a. bare denne innstansen har tilgang
    // Gjør kall på applikasjonen
    // Lås opp applikasjonen igjen, og la andre få tilgang på den.
    // Sjekk om flere skal bruke applikasjonen
    // // Hvis ikke slett applikasjonen

    private final long SHUTDOWN_TIME_DELAY_SECONDS = 300;

    private final ApplicationManager applicationManager;
    private final RestTemplate restTemplate;
    private final ScheduledExecutorService scheduledExecutorService;
    private final String appName;
    private final AtomicInteger numClients;

    public SyntConsumer(ApplicationManager applicationManager, RestTemplate restTemplate, ScheduledExecutorService scheduledExecutorService, String appName) {
        this.applicationManager = applicationManager;
        this.restTemplate = restTemplate;
        this.scheduledExecutorService = scheduledExecutorService;
        this.appName = appName;
        this.numClients = new AtomicInteger(0);
    }

    public Object synthesizeData(RequestEntity request) {
        this.numClients.incrementAndGet();
        applicationManager.startApplication(appName);

        Object synthesizedData = accessSyntPackage(request);

        try {
            return synthesizedData;
        } finally {
            if (this.numClients.get() <= 0) {
                scheduledExecutorService.schedule(this::shutdownApplication, SHUTDOWN_TIME_DELAY_SECONDS, TimeUnit.SECONDS);
            }
        }
    }

    private void shutdownApplication() {
        this.numClients.decrementAndGet();

        if (this.numClients.get() > 0) {
            return;
        }
        applicationManager.shutdownApplication(appName);
    }

    private synchronized Object accessSyntPackage(RequestEntity request) {
        ResponseEntity response = restTemplate.exchange(request, Object.class);
        return response.getBody();
    }
}
