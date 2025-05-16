package no.nav.dolly.budpro.test;

import lombok.AccessLevel;
import lombok.Getter;
import org.springframework.http.HttpStatus;

class EmulatedFailureException extends Exception {

    @Getter(AccessLevel.PACKAGE)
    private final HttpStatus status;

    EmulatedFailureException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

}
