package no.nav.udistub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CouldNotCreatePersonException extends RuntimeException {
	public CouldNotCreatePersonException(String message) {
		super(message);
	}
}
