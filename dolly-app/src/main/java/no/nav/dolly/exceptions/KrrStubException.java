package no.nav.dolly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class KrrStubException extends RuntimeException {

    public KrrStubException(String message, Throwable throwable) {
        super(message, throwable);
    }
}
