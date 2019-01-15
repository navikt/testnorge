package no.nav.registre.skd.exceptions;

public class SkdFunctionalException extends RuntimeException {

    public SkdFunctionalException() {
        super();
    }

    public SkdFunctionalException(String message) {
        super(message);
    }

    public SkdFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }
}
