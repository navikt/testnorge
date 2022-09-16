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

    public static void getCompleteableFuture(CompletableFuture<BestillingProgress> completeableFuture) {
        try {
            completeableFuture.get(60, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
        } catch (ExecutionException e) {
            log.error(e.getMessage(), e);
            Thread.interrupted();
        } catch (TimeoutException e) {
            log.error("Tidsavbrudd (60 s) ved gjenopprett gruppe");
            Thread.interrupted();
        }
    }

}
