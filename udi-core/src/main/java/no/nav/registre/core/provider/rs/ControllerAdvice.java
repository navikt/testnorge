package no.nav.registre.core.provider.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map constraintHandler(ConstraintViolationException exception, HttpServletRequest request) {
        var message = exception.getConstraintViolations().stream().findAny()
                .map(ConstraintViolation::getMessage)
                .orElse("Could not find constraint violation");

        return getValues(exception, request, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map> defaultExceptionHandler(Exception ex, HttpServletRequest request) {
        var annotation = Optional.ofNullable(AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class));
        var status = annotation.map(ResponseStatus::code).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        if (annotation.isEmpty()) {
            log.error("Internal server error", ex);
            return ResponseEntity.status(status).body(getValues(ex, request, "Internal server error", status));
        } else {
            var message = annotation.map(ResponseStatus::reason).filter(StringUtils::hasText).orElse(ex.getMessage());
            return ResponseEntity.status(status).body(getValues(ex, request, message, status));
        }
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
                "status", status
        );
    }
}
