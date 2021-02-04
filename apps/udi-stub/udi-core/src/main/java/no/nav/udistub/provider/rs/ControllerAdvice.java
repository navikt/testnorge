package no.nav.udistub.provider.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.udistub.exception.TpsfHentPersonException;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice extends ResponseEntityExceptionHandler {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(ConstraintViolationException.class)
    public Map constraintHandler(ConstraintViolationException exception, HttpServletRequest request) {
        var message = exception.getConstraintViolations().stream().findAny()
                .map(ConstraintViolation::getMessage)
                .orElse("Could not find constraint violation");

        return getValues(exception, request, message, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ Exception.class, TpsfHentPersonException.class })
    public ResponseEntity<PersonControllerResponse> defaultExceptionHandler(Exception ex, HttpServletRequest request) {
        var annotation = Optional.ofNullable(AnnotationUtils.findAnnotation(ex.getClass(), ResponseStatus.class));
        var status = annotation.map(ResponseStatus::code).orElse(HttpStatus.INTERNAL_SERVER_ERROR);
        if (annotation.isEmpty()) {
            log.error(ex.getMessage(), ex);
            return ResponseEntity.status(status)
                    .body(new PersonControllerResponse(getValues(ex, request, HttpStatus.INTERNAL_SERVER_ERROR.name(), status)));
        } else {
            var message = annotation.map(ResponseStatus::reason)
                    .filter(StringUtils::hasText)
                    .orElse(ex.getMessage());
            return ResponseEntity.status(status).body(new PersonControllerResponse(getValues(ex, request, message, status)));
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
                "status", status.value()
        );
    }
}
