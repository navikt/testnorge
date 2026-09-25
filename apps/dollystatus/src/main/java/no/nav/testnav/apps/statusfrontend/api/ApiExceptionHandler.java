package no.nav.testnav.apps.statusfrontend.api;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestCooldownException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestNotFoundException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestRunInProgressException;
import no.nav.testnav.apps.statusfrontend.functionaltest.exception.FunctionalTestRunNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ServerWebExchange;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

@Slf4j
@RestControllerAdvice(assignableTypes = {
        FagsystemStatusController.class,
        TestkjoringController.class
})
public class ApiExceptionHandler {

    private final Clock clock;

    public ApiExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<ExceptionInformation> badRequest(ServerWebExchange exchange) {
        return informationFor(
                HttpStatus.BAD_REQUEST,
                "Forespørselen har ugyldig format.",
                exchange,
                null);
    }

    @ExceptionHandler({
            FunctionalTestNotFoundException.class,
            FunctionalTestRunNotFoundException.class
    })
    ResponseEntity<ExceptionInformation> notFound(ServerWebExchange exchange) {
        return informationFor(
                HttpStatus.NOT_FOUND,
                "Fagsystemet eller testkjøringen ble ikke funnet.",
                exchange,
                null);
    }

    @ExceptionHandler(FunctionalTestRunInProgressException.class)
    ResponseEntity<ExceptionInformation> conflict(ServerWebExchange exchange) {
        return informationFor(
                HttpStatus.CONFLICT,
                "En annen testkjøring pågår.",
                exchange,
                null);
    }

    @ExceptionHandler(FunctionalTestCooldownException.class)
    ResponseEntity<ExceptionInformation> cooldown(
            FunctionalTestCooldownException exception,
            ServerWebExchange exchange
    ) {
        var retryAfterSeconds = Math.max(
                1,
                Duration.between(clock.instant(), exception.getRetryAfter()).toSeconds());
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .header(HttpHeaders.RETRY_AFTER, Long.toString(retryAfterSeconds))
                .body(information(
                        HttpStatus.TOO_MANY_REQUESTS,
                        "Fagsystemet kan kjøres på nytt etter cooldown-perioden.",
                        exchange,
                        exception.getRetryAfter()));
    }

    @ExceptionHandler(RuntimeException.class)
    ResponseEntity<ExceptionInformation> internalError(
            RuntimeException exception,
            ServerWebExchange exchange
    ) {
        log.error("Uventet feil i dollystatus-API-et: {}", exception.getClass().getSimpleName());
        return informationFor(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Testkjøringen kunne ikke behandles.",
                exchange,
                null);
    }

    private ResponseEntity<ExceptionInformation> informationFor(
            HttpStatus status,
            String message,
            ServerWebExchange exchange,
            Instant retryAfter
    ) {
        return ResponseEntity.status(status)
                .body(information(status, message, exchange, retryAfter));
    }

    private ExceptionInformation information(
            HttpStatus status,
            String message,
            ServerWebExchange exchange,
            Instant retryAfter
    ) {
        return new ExceptionInformation(
                message,
                status.getReasonPhrase(),
                exchange.getRequest().getPath().pathWithinApplication().value(),
                status.value(),
                clock.instant(),
                retryAfter);
    }

    public record ExceptionInformation(
            String message,
            String error,
            String path,
            int status,
            Instant timestamp,
            Instant retryAfter
    ) {
    }
}
