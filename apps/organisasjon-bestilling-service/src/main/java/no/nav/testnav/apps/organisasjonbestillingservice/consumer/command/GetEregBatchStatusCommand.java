package no.nav.testnav.apps.organisasjonbestillingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetEregBatchStatusCommand implements Callable<Long> {
    private final WebClient webClient;
    private final Long batchId;
    private final String token;
    private final String miljo;

    @Override
    public Long call() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/api/v1/batch/items/{id}").build(batchId))
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("miljoe", miljo)
                .retrieve()
                .bodyToMono(Long.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}