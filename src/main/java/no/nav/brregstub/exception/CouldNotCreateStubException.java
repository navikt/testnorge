package no.nav.brregstub.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class CouldNotCreateStubException extends RuntimeException {
	public CouldNotCreateStubException(String message) {
		super(message);
	}
}
