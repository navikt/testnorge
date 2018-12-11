package no.nav.dolly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class KodeverkException extends HttpClientErrorException {

    public KodeverkException(HttpStatus status, String message) {
        super(status, message);
    }

    public KodeverkException(HttpStatus status, String message, Throwable throwable) {
        this(status, message);
    }
}
