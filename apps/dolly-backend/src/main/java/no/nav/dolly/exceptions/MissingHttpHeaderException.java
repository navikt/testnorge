package no.nav.dolly.exceptions;

public class MissingHttpHeaderException extends RuntimeException {

    public MissingHttpHeaderException(String msg) {
        super(msg);
    }
}
