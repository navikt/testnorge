package no.nav.testnav.apps.statusfrontend.functionaltest.exception;

public class FunctionalTestVerificationTimeoutException extends RuntimeException {

    public FunctionalTestVerificationTimeoutException() {
        super("Verifiseringen nådde tidsfristen.");
    }
}
