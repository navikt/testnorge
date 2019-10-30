package no.nav.registre.aareg.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingHttpHeaderException extends RuntimeException {

    public MissingHttpHeaderException(String msg) {
        super(msg);
    }
}
