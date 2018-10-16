package no.nav.identpool.ident.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.SERVICE_UNAVAILABLE)
public class ForFaaLedigeIdenterException extends Exception {
    public ForFaaLedigeIdenterException(String message) {
        super(message);
    }
}
