package no.nav.registre.skd.exceptions;

public class KunneIkkeSendeTilTpsException extends RuntimeException {
    public KunneIkkeSendeTilTpsException() {
        super();
    }

    public KunneIkkeSendeTilTpsException(String message) {
        super(message);
    }

    public KunneIkkeSendeTilTpsException(String message, Throwable cause) {
        super(message, cause);
    }
}
