package no.nav.dolly.exceptions;

public class DollyFunctionalException extends RuntimeException {

    public DollyFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }

    public DollyFunctionalException(String message) {
        super(message);
    }
}