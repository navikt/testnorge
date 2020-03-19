package no.nav.registre.inntekt.utils;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Getter
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ValidationException extends Exception {
    private final List<String> errors;

    public ValidationException(List<String> errors) { this.errors = errors; }
}
