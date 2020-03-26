package no.nav.registre.arena.core.security;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
public class TokenServiceException extends RuntimeException {

    public TokenServiceException(
            Exception cause
    ) {
        super(cause);
    }
}
