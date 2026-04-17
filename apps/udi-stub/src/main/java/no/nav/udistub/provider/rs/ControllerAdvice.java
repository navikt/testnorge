package no.nav.udistub.provider.rs;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import no.nav.udistub.exception.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    private final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    public Map<String, Object> notFoundHandler(NotFoundException exception, HttpServletRequest request) {
        log.warn("Ressurs ikke funnet: {}", exception.getMessage());
        return getValues(exception, request, exception.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map<String, Object> constraintHandler(ConstraintViolationException exception, HttpServletRequest request) {
        var message = exception.getConstraintViolations().stream().findAny()
                .map(ConstraintViolation::getMessage)
                .orElse("Could not find constraint violation");

        log.warn("Constraint violation: {}", message);
        return getValues(exception, request, message, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public Map<String, Object> generalExceptionHandler(Exception exception, HttpServletRequest request) {
        log.error("Uventet feil ved kall til {}: {}", request.getRequestURI(), exception.getMessage(), exception);
        return getValues(exception, request, exception.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Map<String, Object> getValues(Exception exception,
                                          HttpServletRequest request,
                                          String message,
                                          HttpStatus status) {
        return Map.of(
                "timestamp", ZonedDateTime.now().format(dateTimeFormatter),
                "error", exception.getClass().getSimpleName(),
                "path", request.getRequestURI(),
                "message", message,
                "status", status.value()
        );
    }
}
