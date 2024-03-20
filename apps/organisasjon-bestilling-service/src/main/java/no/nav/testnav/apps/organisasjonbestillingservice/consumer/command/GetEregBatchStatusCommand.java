package no.nav.testnav.apps.organisasjonbestillingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetEregBatchStatusCommand implements Callable<Long> {
    private final WebClient webClient;
    private final Long batchId;
    private final String token;
    private final String miljo;

    @Override
    public Long call() {
        try {
            return webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/api/v1/batch/items/{id}").build(batchId))
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header("miljoe", miljo)
                    .retrieve()
                    .bodyToMono(Long.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(throwable -> throwable instanceof WebClientResponseException.GatewayTimeout))
                    .block();
        } catch (Exception e) {
            log.error("Failed to get status for batch with id: " + batchId, e);
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Resource not found", e);
        }
    }
}