package no.nav.registre.hodejegeren.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class TomResponsFraTpsSyntException extends HodejegerenFunctionalException {

    public TomResponsFraTpsSyntException() {
        super();
    }

    public TomResponsFraTpsSyntException(String message) {
        super(message);
    }
}
