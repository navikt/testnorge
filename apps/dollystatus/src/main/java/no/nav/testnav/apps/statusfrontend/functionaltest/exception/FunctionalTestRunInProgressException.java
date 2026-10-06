package no.nav.testnav.apps.statusfrontend.functionaltest.exception;

public class FunctionalTestRunInProgressException extends RuntimeException {

    public FunctionalTestRunInProgressException() {
        super("En funksjonstestkjøring pågår.");
    }
}
