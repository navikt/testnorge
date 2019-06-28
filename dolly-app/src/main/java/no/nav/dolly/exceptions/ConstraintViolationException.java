package no.nav.dolly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ConstraintViolationException extends RuntimeException {

    public ConstraintViolationException(String msg) {
        super(msg);
    }

    public ConstraintViolationException(String msg, Throwable cause) {
        super(msg, cause);
    }
}
