package no.nav.registre.hodejegeren.exception;

public class ManglendeInfoITpsException extends RuntimeException {

    public ManglendeInfoITpsException() {
        super();
    }

    public ManglendeInfoITpsException(String message) {
        super(message);
    }

    public ManglendeInfoITpsException(String message, Throwable cause) {
        super(message, cause);
    }
}
