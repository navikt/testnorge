package no.nav.testnav.apps.statusfrontend.functionaltest.exception;

public class FunctionalTestExistingDataException extends RuntimeException {

    public FunctionalTestExistingDataException() {
        super("Eksisterende testdata må ryddes før oppretting.");
    }
}
