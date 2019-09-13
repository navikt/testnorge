package no.nav.dolly.bestilling.udistub;

public class UdiStubException extends RuntimeException {
    public UdiStubException(Exception cause) {
        super(cause);
    }

    public UdiStubException(String message) {
        super(message);
    }
}