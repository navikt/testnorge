package no.nav.testnav.pdllagreservice.exception;

public class UnrecoverableException extends RuntimeException {

    public UnrecoverableException(String message, Exception e) {
        super(message, e);
    }
}
