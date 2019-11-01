package no.nav.registre.syntrest.consumer;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.syntrest.kubernetes.ApplicationManager;
import no.nav.registre.syntrest.utils.SyntAppNames;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Getter
public class SyntConsumer {

    private final ApplicationManager applicationManager;
    private final RestTemplate restTemplate;
    // private final ScheduledExecutorService scheduledExecutorService;
    private final String appName;
    // private final AtomicInteger numClients;

    // private ScheduledFuture<?> scheduledFutureTakedownApplication;

    public SyntConsumer(ApplicationManager applicationManager, RestTemplate restTemplate, ScheduledExecutorService scheduledExecutorService, SyntAppNames name) {
        this.applicationManager = applicationManager;
        this.restTemplate = restTemplate;
        // this.scheduledExecutorService = scheduledExecutorService;
        this.appName = name.getName();
        // this.numClients = new AtomicInteger(0);
        // this.scheduledFutureTakedownApplication = null;
    }

    public Object synthesizeData(RequestEntity request) {
        // this.numClients.incrementAndGet();

        if (!applicationManager.applicationIsAlive(appName)) {
            int started = applicationManager.startApplication(appName);
            if (started == -1) {
                log.error("Could not start synth package {}", this.appName);
                return new ResponseEntity<>("Something went wrong when trying to deploy the synth pacakge.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }

        Object synthesizedData = accessSyntPackage(request);
        applicationManager.updateAccessedPackages(this);

        return synthesizedData;
        /*try {
            return synthesizedData;
        } finally {
            this.numClients.decrementAndGet();
            System.out.println("Number of connected clients after this: " + this.numClients.get());
            if (this.numClients.get() <= 0) {
                log.info("Scheduling shutdown of {}", this.appName);
                *//*if (!this.scheduledExecutorService.isShutdown()) {
                    this.scheduledExecutorService.schedule(this::shutdownApplication, SHUTDOWN_TIME_DELAY_SECONDS, TimeUnit.SECONDS);
                } else {
                    log.info("More clients were connected to \'{}\' before shutdown. Resetting shutdown timer.", this.appName);
                    this.scheduledExecutorService.shutdown();
                    this.scheduledExecutorService.schedule(this::shutdownApplication, SHUTDOWN_TIME_DELAY_SECONDS, TimeUnit.SECONDS);
                }*//*
                if (Objects.isNull(this.scheduledFutureTakedownApplication.get()) || )
                this.scheduledFutureTakedownApplication = this.scheduledExecutorService.schedule(
                        this::shutdownApplication,
                        SHUTDOWN_TIME_DELAY_SECONDS,
                        TimeUnit.SECONDS);
            }
        }*/
    }

    public void shutdownApplication() {
        applicationManager.shutdownApplication(appName);
    }

    /*private void shutdownApplication() {
        *//*this.numClients.decrementAndGet();*//*
        if (this.numClients.get() > 0) {
            log.info("More clients were connected to {} before shutdown.", this.appName);
            return;
        }
        log.info("Shutting down {}", this.appName);
        applicationManager.shutdownApplication(appName);
    }*/

    // In the syntConsumer because we will allow synt packages of other types to be accessed asynchronously,
    // but calls to the *same SyntPackage* should happen one at a time.
    private synchronized Object accessSyntPackage(RequestEntity request) {
        ResponseEntity response = restTemplate.exchange(request, Object.class);
        return response.getBody();
    }
}
