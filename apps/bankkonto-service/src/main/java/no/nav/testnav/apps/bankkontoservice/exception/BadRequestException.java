package no.nav.testnav.apps.bankkontoservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class BadRequestException extends HttpClientErrorException {

    public BadRequestException(String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
