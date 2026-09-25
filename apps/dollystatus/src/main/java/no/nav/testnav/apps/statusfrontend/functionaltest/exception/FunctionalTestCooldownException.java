package no.nav.testnav.apps.statusfrontend.functionaltest.exception;

import java.time.Instant;

public class FunctionalTestCooldownException extends RuntimeException {

    private final Instant retryAfter;

    public FunctionalTestCooldownException(Instant retryAfter) {
        super("Funksjonstesten er i cooldown.");
        this.retryAfter = retryAfter;
    }

    public Instant getRetryAfter() {
        return retryAfter;
    }
}
