package no.nav.registre.bisys.adapter.exception;

public class PersonSearchException extends RuntimeException {
    public PersonSearchException(String message, Exception e) {
        super(message, e);
    }
}