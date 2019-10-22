package no.nav.registre.aareg.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class FasitException extends HttpClientErrorException {

    public FasitException(HttpStatus status, String message) {
        super(status, message);
    }
}
