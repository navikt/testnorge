package no.nav.registre.aareg.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.BAD_REQUEST)
public class TestnorgeAaregFunctionalException extends RuntimeException {

    public TestnorgeAaregFunctionalException(String message, Throwable cause) {
        super(message, cause);
    }

    public TestnorgeAaregFunctionalException(String message) {
        super(message);
    }
}