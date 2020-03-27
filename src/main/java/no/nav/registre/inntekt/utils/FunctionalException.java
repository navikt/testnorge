package no.nav.registre.inntekt.utils;

public class FunctionalException extends RuntimeException {

    public FunctionalException(String message, Throwable cause) {
        super(message, cause);
    }

    public FunctionalException(String message) {
        super(message);
    }
}
