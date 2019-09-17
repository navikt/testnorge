package no.nav.registre.syntrest.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import no.nav.registre.syntrest.utils.SyntAppNames;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class SyntConsumer {

    private final long SHUTDOWN_TIME_DELAY_SECONDS = 300;

    private final ApplicationManager applicationManager;
    private final RestTemplate restTemplate;
    private final ScheduledExecutorService scheduledExecutorService;
    private final String appName;
    private final AtomicInteger numClients;

    public SyntConsumer(ApplicationManager applicationManager, RestTemplate restTemplate, ScheduledExecutorService scheduledExecutorService, SyntAppNames name) {
        this.applicationManager = applicationManager;
        this.restTemplate = restTemplate;
        this.scheduledExecutorService = scheduledExecutorService;
        this.appName = name.getName();
        this.numClients = new AtomicInteger(0);
    }

    public Object synthesizeData(RequestEntity request) {
        this.numClients.incrementAndGet();

        if (!applicationManager.applicationIsAlive(appName)) {
            applicationManager.startApplication(appName);
        }

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

    // In the syntConsumer because we will allow synt packages of other types to be accessed asynchronously,
    // but calls to the *same SyntPackage* should happen one at a time.
    private synchronized Object accessSyntPackage(RequestEntity request) {
        ResponseEntity response = restTemplate.exchange(request, Object.class);
        return response.getBody();
    }
}
