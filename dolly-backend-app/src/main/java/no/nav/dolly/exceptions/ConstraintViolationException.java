package no.nav.dolly.exceptions;

public class ConstraintViolationException extends RuntimeException {

    public ConstraintViolationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
