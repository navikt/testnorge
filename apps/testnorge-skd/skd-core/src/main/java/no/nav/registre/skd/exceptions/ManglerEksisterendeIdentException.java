package no.nav.registre.skd.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.CONFLICT)
public class ManglerEksisterendeIdentException extends SkdFunctionalException{

    public ManglerEksisterendeIdentException(String message) {
        super(message);
    }
}
