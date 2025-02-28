package no.nav.testnav.libs.reactivecore.utils;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.function.Predicate;

@UtilityClass
@Slf4j
public class Errors {

    public static Mono<Throwable> handle(ClientResponse response) {
        return response
                .bodyToMono(String.class)
                .flatMap(errorBody -> {
                    var httpStatus = HttpStatus.resolve(response.statusCode().value());
                    var reasonPhrase = (httpStatus != null) ? httpStatus.getReasonPhrase() : "Unknown Status";
                    var errorMessage = "Error response %d: %s".formatted(response.statusCode().value(), errorBody);
                    log.error(errorMessage);
                    return Mono.error(
                            new WebClientResponseException(
                                    errorMessage,
                                    response.statusCode().value(),
                                    reasonPhrase,
                                    response.headers().asHttpHeaders(),
                                    errorBody.getBytes(),
                                    null
                            ));
                });
    }

    public static Retry are(Predicate<Throwable> cause) {
        return Retry
                .backoff(3, Duration.ofSeconds(5))
                .filter(cause);
    }

}
