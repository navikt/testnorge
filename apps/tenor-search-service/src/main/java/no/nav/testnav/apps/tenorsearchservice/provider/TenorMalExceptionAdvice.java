package no.nav.testnav.apps.tenorsearchservice.provider;

import lombok.Builder;
import no.nav.testnav.apps.tenorsearchservice.exception.BrukerServiceUnavailableException;
import no.nav.testnav.apps.tenorsearchservice.exception.TenorMalConflictException;
import no.nav.testnav.apps.tenorsearchservice.exception.TenorMalNotFoundException;
import no.nav.testnav.apps.tenorsearchservice.exception.TenorMalValidationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebInputException;

import java.time.LocalDateTime;

@RestControllerAdvice(assignableTypes = TenorPersonMalController.class)
public class TenorMalExceptionAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler({
            TenorMalValidationException.class,
            WebExchangeBindException.class,
            ServerWebInputException.class
    })
    ExceptionInformation badRequest(RuntimeException exception, ServerWebExchange exchange) {
        return informationForException(
                HttpStatus.BAD_REQUEST,
                exception instanceof TenorMalValidationException ?
                        exception.getMessage() :
                        "Forespørselen har ugyldig format.",
                exchange);
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TenorMalNotFoundException.class)
    ExceptionInformation notFound(ServerWebExchange exchange) {
        return informationForException(
                HttpStatus.NOT_FOUND,
                "Malen ble ikke funnet.",
                exchange);
    }

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler({
            TenorMalConflictException.class,
            DataIntegrityViolationException.class
    })
    ExceptionInformation conflict(ServerWebExchange exchange) {
        return informationForException(
                HttpStatus.CONFLICT,
                "En mal med dette navnet finnes allerede på brukeren din.",
                exchange);
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    ExceptionInformation forbidden(ServerWebExchange exchange) {
        return informationForException(
                HttpStatus.FORBIDDEN,
                "Noe gikk galt med autentiseringen.",
                exchange);
    }

    @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
    @ExceptionHandler(BrukerServiceUnavailableException.class)
    ExceptionInformation serviceUnavailable(ServerWebExchange exchange) {
        return informationForException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Tilgjengelige malbrukere kunne ikke hentes.",
                exchange);
    }

    private static ExceptionInformation informationForException(
            HttpStatus status,
            String message,
            ServerWebExchange exchange
    ) {
        return ExceptionInformation.builder()
                .message(message)
                .error(status.getReasonPhrase())
                .path(exchange.getRequest().getURI().getPath())
                .status(status.value())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Builder
    public record ExceptionInformation(
            String message,
            String error,
            String path,
            Integer status,
            LocalDateTime timestamp
    ) {
    }
}
