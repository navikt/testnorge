package no.nav.registre.testnorge.arbeidsforholdservice.exception;

public class SikkerhetsTokenExpiredException extends RuntimeException {

    public SikkerhetsTokenExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
