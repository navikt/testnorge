package no.nav.dolly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
public class SigrunnStubException extends RuntimeException{

    public SigrunnStubException(String message) {
        super(message);
    }

}
