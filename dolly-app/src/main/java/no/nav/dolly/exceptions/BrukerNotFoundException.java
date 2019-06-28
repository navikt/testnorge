package no.nav.dolly.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND)
public class BrukerNotFoundException extends NotFoundException {

    public BrukerNotFoundException(String message) {
        super(message);
    }

}
