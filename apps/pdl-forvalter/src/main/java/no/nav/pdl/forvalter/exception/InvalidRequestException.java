package no.nav.pdl.forvalter.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class InvalidRequestException extends HttpClientErrorException {

    public InvalidRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
