package no.nav.registre.hodejegeren.exception;

import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus
public class ManglerEksisterendeIdentException extends HodejegerenFunctionalException {

    public ManglerEksisterendeIdentException() {
        super();
    }

    public ManglerEksisterendeIdentException(String message) {
        super(message);
    }
}
