package no.nav.dolly.web.service;

import org.springframework.security.authentication.CredentialsExpiredException;

public class SessionExpiredException extends CredentialsExpiredException {

    public SessionExpiredException(String message) {
        super(message);
    }

    public SessionExpiredException(String message, Throwable cause) {
        super(message, cause);
    }
}
