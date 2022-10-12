package no.nav.dolly.bestilling.service;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.jpa.BestillingProgress;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@UtilityClass
@Slf4j
public final class GjenopprettUtil {

    public static void executeCompleteableFuture(CompletableFuture<BestillingProgress> completeableFuture) {
        executeCompleteableFuture(completeableFuture, 60);
    }

    public static void executeCompleteableFuture(CompletableFuture<BestillingProgress> completeableFuture, int secondsTimeout) {
        try {
            completeableFuture.get(secondsTimeout, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (TimeoutException e) {
            log.error(String.format("Tidsavbrudd (%d s) ved gjenopprett", secondsTimeout));
            Thread.currentThread().interrupt();
        }
    }

}
