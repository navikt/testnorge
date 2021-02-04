package no.nav.udistub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TpsfHentPersonException extends RuntimeException {
    public TpsfHentPersonException(String message) {
        super(message);
    }
}
