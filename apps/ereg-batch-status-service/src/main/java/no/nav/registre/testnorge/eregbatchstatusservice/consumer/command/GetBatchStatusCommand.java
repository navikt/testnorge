package no.nav.registre.testnorge.eregbatchstatusservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.eregbatchstatusservice.util.WebClientFilter;
import org.apache.hc.core5.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class GetBatchStatusCommand implements Callable<Long> {
    private final WebClient webClient;
    private final Long id;

    @Override
    public Long call() {
        Long response = webClient
                .mutate() // Create a new WebClient instance with the same settings
                .defaultHeaders(headers -> headers.remove(HttpHeaders.AUTHORIZATION)) // Remove the Authorization header
                .build()
                .get()
                .uri(builder -> builder.path("/ereg/internal/batch/poll/{id}").build(id))
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
                        clientResponse.headers().asHttpHeaders().forEach((name, values) -> {
                            log.info("Header: " + name + " Values: " + values);
                        });
                        log.error("Unauthorized error occurred when calling EREG");
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));
                    }
                    log.error("Client error occurred when calling EREG");
                    return Mono.error(new ResponseStatusException(clientResponse.statusCode(), "Client error occurred"));
                })
                .bodyToMono(Long.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
        log.info("Mottok statuskode fra Batch-Ereg: " + response);
        return response;
    }
}
