package no.nav.testnav.apps.personservice.consumer.v1.exception;

public class PdlCreatePersonException extends RuntimeException {

    public PdlCreatePersonException(String message, Exception e) {
        super(message, e);
    }
}
