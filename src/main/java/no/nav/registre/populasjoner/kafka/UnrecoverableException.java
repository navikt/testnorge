package no.nav.registre.populasjoner.kafka;

public class UnrecoverableException extends RuntimeException {

    public UnrecoverableException(String message) {
        super(message);
    }

    public UnrecoverableException(
            String message,
            Exception e
    ) {
        super(message, e);
    }
}
