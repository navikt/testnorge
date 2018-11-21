package no.nav.identpool.rs.v1;

import java.time.DateTimeException;

import no.nav.identpool.rs.v1.support.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;

@ControllerAdvice
@RequestMapping(produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class IdentpoolControllerAdvice {
    /** TODO Denne har ingen misjon slik som den er satt opp nå ettersom den ikke fanger opp noen exception som faktisk kan bli kastet
     Enten slett denne eller flytt håndtering av HttpStatus ut fra de forskjellige exceptiontypene hit og slett de som ikke er aktuelle */

    @ExceptionHandler(IllegalArgumentException.class)
    private ResponseEntity<ApiError> illegalArgumentException(IllegalArgumentException e) {
        return error(e, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DateTimeException.class)
    private ResponseEntity<ApiError> dateTimeException(DateTimeException e) {
        return error(e, HttpStatus.BAD_REQUEST);
    }

    private ResponseEntity<ApiError> error(Exception e, HttpStatus httpStatus) {
        return ResponseEntity.status(httpStatus).body(new ApiError(e.getMessage(), httpStatus));
    }

}
