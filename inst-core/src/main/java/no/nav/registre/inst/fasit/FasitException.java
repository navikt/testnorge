package no.nav.registre.inst.fasit;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class FasitException extends HttpClientErrorException {

    public FasitException(HttpStatus status, String message, Throwable throwable) {
        super(status, message);
    }
}
