package no.nav.registre.testnorge.batchbestillingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetAktiveBestillingerCommand implements Callable<Flux<Object>> {
    private final WebClient webClient;
    private final String token;
    private final Long gruppeId;

    @Override
    public Flux<Object> call() {
        final String HEADER_NAV_CALL_ID = "Nav-Call-Id";
        final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";

        log.info("Henter aktive bestillinger for gruppe {}.", gruppeId);
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/bestilling/gruppe/{gruppeId}/ikkeferdig")
                        .build(gruppeId)
                )
                .header(HEADER_NAV_CALL_ID, "Batch-bestilling-service")
                .header(HEADER_NAV_CONSUMER_ID, "Batch-bestilling-service")
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(Object.class)
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> {
                            log.warn("Fant ikke gruppe {}.", gruppeId);
                            return Mono.empty();
                        })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
