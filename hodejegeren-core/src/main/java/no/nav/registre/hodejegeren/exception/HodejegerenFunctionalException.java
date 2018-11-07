package no.nav.registre.hodejegeren.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class HodejegerenFunctionalException extends RuntimeException {

    public HodejegerenFunctionalException() {
        super();
    }

    public HodejegerenFunctionalException(String message) {
        super(message);
    }

    public HodejegerenFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }
}
