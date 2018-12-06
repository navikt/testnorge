package no.nav.registre.hodejegeren.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ManglerEksisterendeIdentException extends HodejegerenFunctionalException {

    public ManglerEksisterendeIdentException() {
        super();
    }

    public ManglerEksisterendeIdentException(String message) {
        super(message);
    }
}
