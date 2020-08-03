package no.nav.registre.spion.exception;

import com.fasterxml.jackson.core.JsonProcessingException;

public class PubliserVedtakException extends JsonProcessingException {

    public PubliserVedtakException(String message) {
        super(message);
    }

    public PubliserVedtakException(String message, Throwable cause) {
        super(message, cause);
    }
}
