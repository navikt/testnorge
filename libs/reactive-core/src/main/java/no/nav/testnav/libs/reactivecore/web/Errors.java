package no.nav.testnav.libs.reactivecore.web;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.Contract;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Predicate;

@UtilityClass
@Slf4j
public class Errors {

    public static Mono<Throwable> handle(ClientResponse response) {
        var request = response.request();
        var exception = new ResponseStatusException(response.statusCode());
        log.error("CATO3: HTTP {} to {} gave {}", request.getMethod(), request.getURI(), exception.getMessage(), exception);
        return Mono.error(exception);
    }

    public static Retry retry(Predicate<Throwable> on, long times, long afterSeconds) {
        return Retry
                .backoff(times, Duration.ofSeconds(afterSeconds))
                .filter(on);
    }

    public static Retry retry(Predicate<Throwable> on) {
        return retry(on, 3, 5);
    }

    public static Consumer<HttpHeaders> headers(String token) {
        return headers -> headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + token);
    }

}
