package no.nav.dolly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

public class JiraException extends HttpClientErrorException {

    public JiraException(HttpStatus statusCode, String statusText) {
        super(statusCode, statusText);
    }
}
