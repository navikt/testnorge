package no.nav.identpool.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class UgyldigDatoException extends Exception {
    public UgyldigDatoException(String message) {
        super(message);
    }
}
