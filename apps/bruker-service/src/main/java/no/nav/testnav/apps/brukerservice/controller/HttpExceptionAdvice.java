package no.nav.testnav.apps.brukerservice.controller;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.brukerservice.exception.JwtIdMismatchException;
import no.nav.testnav.apps.brukerservice.exception.UserAlreadyExistsException;
import no.nav.testnav.apps.brukerservice.exception.UserHasNoAccessToOrgnisasjonException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@ControllerAdvice
@Slf4j
@RequiredArgsConstructor
public class HttpExceptionAdvice {

    @ResponseBody
    @ExceptionHandler({ WebClientResponseException.NotFound.class })
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public String notFoundRequest(RuntimeException e) {
        return e.getMessage();
    }

    @ExceptionHandler({
            UserAlreadyExistsException.class,
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public String conflictHandler(Exception e) {
        log.trace("CONFLICT: {}", e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler(TokenExpiredException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public String tokenExpiredHandler(Exception e) {
        log.trace("UNAUTHORIZED: {}", e.getMessage());
        return e.getMessage();
    }

    @ExceptionHandler({
            JWTVerificationException.class,
            JwtIdMismatchException.class,
            UserHasNoAccessToOrgnisasjonException.class
    })
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public String forbiddenHandler(Exception e) {
        log.trace("FORBIDDEN: {}", e.getMessage());
        return e.getMessage();
    }

}
