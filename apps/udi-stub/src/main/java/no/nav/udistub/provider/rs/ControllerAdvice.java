package no.nav.udistub.provider.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, Object> constraintHandler(ConstraintViolationException exception, HttpServletRequest request) {
        var message = exception.getConstraintViolations().stream().findAny()
                .map(ConstraintViolation::getMessage)
                .orElse("Could not find constraint violation");

        return getValues(exception, request, message);
    }

    private Map<String, Object> getValues(Exception exception,
                                          HttpServletRequest request,
                                          String message) {
        return Map.of(
                "timestamp", ZonedDateTime.now().format(dateTimeFormatter),
                "error", exception.getClass().getSimpleName(),
                "path", request.getRequestURI(),
                "message", message,
                "status", HttpStatus.BAD_REQUEST.value()
        );
    }
}
