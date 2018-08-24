package no.nav.dolly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class TpsfException extends RuntimeException{

    public TpsfException(String message) {
        super(message);
    }

}
