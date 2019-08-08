package no.nav.registre.core.provider.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@RestControllerAdvice
@Slf4j
public class ControllerAdvice {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map constraintHandler(ConstraintViolationException exception, HttpServletRequest request) {
        var message = exception.getConstraintViolations().stream().findAny()
                .map(ConstraintViolation::getMessage)
                .orElse("Could not find constraint violation");

        return values(exception, request, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map> defaultExceptionHandler(Exception exception, HttpServletRequest request) {
        var annotation = Optional.ofNullable(AnnotationUtils.findAnnotation(exception.getClass(), ResponseStatus.class));

        var status = annotation.map(ResponseStatus::code).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        var message = annotation.map(ResponseStatus::reason).orElse("Internal server error");

        if (message.isBlank()) {
            message = exception.getMessage();
        } else if (annotation.isEmpty()) {
            log.error(message, exception);
        }

        return ResponseEntity.status(status).body(values(exception, request, message, status));
    }

    private Map<String, Object> values(Exception exception,
                                       HttpServletRequest request,
                                       String message,
                                       HttpStatus status) {
        return Map.of(
                "timestamp", ZonedDateTime.now().format(dateTimeFormatter),
                "error", exception.getClass().getSimpleName(),
                "path", request.getRequestURI(),
                "message", message,
                "status", status
        );
    }
}
