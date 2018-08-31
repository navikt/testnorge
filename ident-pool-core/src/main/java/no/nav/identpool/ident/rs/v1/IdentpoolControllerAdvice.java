package no.nav.identpool.ident.rs.v1;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class IdentpoolControllerAdvice {
    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<ApiError> illegalArgumentException(IllegalArgumentException e) {
        return error(e, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ApiError> error(IllegalArgumentException e, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(new ApiError(e.getMessage(), httpStatus));
    }
}
