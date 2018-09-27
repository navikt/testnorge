package no.nav.identpool.ident.rest.v1;

import java.time.DateTimeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

import no.nav.identpool.ident.exception.IdentAlleredeIBrukException;
import no.nav.identpool.ident.exception.UgyldigPersonidentifikatorException;

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


    @ExceptionHandler(DateTimeException.class)
    private ResponseEntity<ApiError> dateTimeException(DateTimeException e) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST));
    }

//    @ExceptionHandler(IdentAlleredeIBrukException.class)
//    private ResponseEntity<ApiError> identAlleredeIBrukException(IdentAlleredeIBrukException e) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiError(e.getMessage(), HttpStatus.BAD_REQUEST));
//    }


}
