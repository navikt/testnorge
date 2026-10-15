package no.nav.dolly.synt.meldekort.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebInputException;

import java.util.Map;

@RestControllerAdvice
@Slf4j
class MeldekortExceptionHandler {

    @ExceptionHandler({ServerWebInputException.class, IllegalArgumentException.class})
    ResponseEntity<Map<String, String>> handleBadRequest(Exception ex) {
        log.warn("Invalid request", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(Map.of("error", "Invalid request", "details", ex.getMessage()));

    }

    @ExceptionHandler(IllegalStateException.class)
    ResponseEntity<Map<String, String>> handleStateExceptions(RuntimeException ex) {
        log.error("Inference failure", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Inference failure", "details", "hidden"));
    }

}

