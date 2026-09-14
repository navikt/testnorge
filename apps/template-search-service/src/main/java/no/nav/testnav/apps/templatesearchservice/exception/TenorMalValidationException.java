package no.nav.testnav.apps.templatesearchservice.exception;

public class TenorMalValidationException extends RuntimeException {

    public TenorMalValidationException(String message) {
        super(message);
    }

    public TenorMalValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
