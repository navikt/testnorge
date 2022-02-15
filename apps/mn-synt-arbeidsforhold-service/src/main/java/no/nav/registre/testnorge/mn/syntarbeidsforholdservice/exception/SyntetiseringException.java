package no.nav.registre.testnorge.mn.syntarbeidsforholdservice.exception;

public class SyntetiseringException extends RuntimeException {

    public SyntetiseringException(String message, Throwable cause) {
        super(message, cause);
    }

    public SyntetiseringException(String message) {
        super(message);
    }
}
