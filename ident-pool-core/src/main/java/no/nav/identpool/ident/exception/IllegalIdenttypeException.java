package no.nav.identpool.ident.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class IllegalIdenttypeException extends Exception {
    public IllegalIdenttypeException(String message) {
        super(message);
    }
}
