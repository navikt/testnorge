package no.nav.identpool.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class IdentAlleredeIBrukException extends Exception {
    public IdentAlleredeIBrukException(String message) {
        super(message);
    }
}
