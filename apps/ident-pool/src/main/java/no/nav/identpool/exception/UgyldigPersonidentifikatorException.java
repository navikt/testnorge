package no.nav.identpool.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UgyldigPersonidentifikatorException extends RuntimeException {
    public UgyldigPersonidentifikatorException(String message) {
        super(message);
    }
}
