package no.nav.testnav.apps.statusfrontend.functionaltest.exception;

import lombok.Getter;

import java.time.Instant;

@Getter
public class FunctionalTestCooldownException extends RuntimeException {

    private final Instant retryAfter;

    public FunctionalTestCooldownException(Instant retryAfter) {
        super("Funksjonstesten er i cooldown.");
        this.retryAfter = retryAfter;
    }
}
