package no.nav.testnav.apps.statusfrontend.functionaltest.exception;

public class FunctionalTestBlockedException extends RuntimeException {

    public FunctionalTestBlockedException() {
        super("Funksjonstesten er blokkert.");
    }
}
