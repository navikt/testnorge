package no.nav.testnav.apps.brukerservice.exception;

public class JwtIdMismatchException extends RuntimeException {
    public JwtIdMismatchException() {
        super("Bruker-ID samsvarer ikke med autentisert bruker.");
    }
}
