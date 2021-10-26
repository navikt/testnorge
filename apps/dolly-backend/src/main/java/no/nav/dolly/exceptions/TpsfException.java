package no.nav.dolly.exceptions;

public class TpsfException extends RuntimeException {

    public TpsfException(String message, Throwable throwable) {
        super(message, throwable);
    }

    public TpsfException(String message) {
        this(message, null);
    }
}
