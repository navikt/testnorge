package no.nav.registre.endringsmeldinger.consumer.rs.exceptions;

public class SyntetiseringsException extends RuntimeException {

    public SyntetiseringsException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }
}
