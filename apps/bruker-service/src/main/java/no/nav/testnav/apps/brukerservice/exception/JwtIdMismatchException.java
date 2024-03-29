package no.nav.testnav.apps.brukerservice.exception;

public class JwtIdMismatchException extends RuntimeException {
    public JwtIdMismatchException(String id, String generatedId) {
        super("Det er en mismatch mellom id " + id + " og generert id " + generatedId + ".");
    }
}
