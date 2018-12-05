package no.nav.registre.hodejegeren.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class TomResponsFraTpsSyntException extends HodejegerenFunctionalException {

    public TomResponsFraTpsSyntException() {
        super();
    }

    public TomResponsFraTpsSyntException(String message) {
        super(message);
    }
}
