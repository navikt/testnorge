package no.nav.registre.testnorge.eregbatchstatusservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.eregbatchstatusservice.util.WebClientFilter;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Slf4j
public class GetBatchStatusCommand implements Callable<Mono<Long>> {
    private final WebClient webClient;
    private final String miljoe;
    private final Long id;
    private final String token;

    @Override
    public Mono<Long> call() {
        return webClient
                .get()
                .uri(builder -> builder.path("/{miljoe}/ereg/internal/batch/poll/{id}").build(miljoe, id))
                .header(AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.UNAUTHORIZED)) {
                        log.error("Unauthorized error occurred when calling modapp-ereg-proxy");
                        return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found"));
                    }
                    log.error("Client error occurred when calling modapp-ereg-proxy");
                    return Mono.error(new ResponseStatusException(clientResponse.statusCode(), "Client error occurred"));
                })
                .bodyToMono(Long.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
