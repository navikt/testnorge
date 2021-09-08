package no.nav.testnav.apps.personservice.consumer.exception;

public class PdlCreatePersonException extends RuntimeException {

    public PdlCreatePersonException(String message, Exception e) {
        super(message, e);
    }
}
