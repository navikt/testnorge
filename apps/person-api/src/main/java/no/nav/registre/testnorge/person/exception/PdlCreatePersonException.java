package no.nav.registre.testnorge.person.exception;

public class PdlCreatePersonException extends RuntimeException {

    public PdlCreatePersonException(String message, Exception e) {
        super(message, e);
    }
}
