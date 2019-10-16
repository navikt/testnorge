package no.nav.dolly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class DollyFunctionalException extends RuntimeException {

    public DollyFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }

    public DollyFunctionalException(String message) {
        super(message);
    }
}