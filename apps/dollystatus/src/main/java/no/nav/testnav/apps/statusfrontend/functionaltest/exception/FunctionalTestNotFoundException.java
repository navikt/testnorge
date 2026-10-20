package no.nav.testnav.apps.statusfrontend.functionaltest.exception;

public class FunctionalTestNotFoundException extends RuntimeException {

    public FunctionalTestNotFoundException() {
        super("Fagsystemet finnes ikke.");
    }
}
