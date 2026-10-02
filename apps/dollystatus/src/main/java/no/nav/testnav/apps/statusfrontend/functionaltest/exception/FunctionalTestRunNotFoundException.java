package no.nav.testnav.apps.statusfrontend.functionaltest.exception;

public class FunctionalTestRunNotFoundException extends RuntimeException {

    public FunctionalTestRunNotFoundException() {
        super("Testkjøringen finnes ikke.");
    }
}
