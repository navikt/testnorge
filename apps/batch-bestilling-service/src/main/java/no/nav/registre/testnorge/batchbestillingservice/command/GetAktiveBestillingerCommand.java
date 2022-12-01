package no.nav.registre.testnorge.batchbestillingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetAktiveBestillingerCommand implements Callable<Flux<Object>> {
    private final WebClient webClient;
    private final String token;
    private final Long gruppeId;
    private final Boolean sendToProd;
    private final ServerProperties serviceProperties;
    private final ServerProperties devServiceProperties;

    @Override
    public Flux<Object> call() {
        final String HEADER_NAV_CALL_ID = "Nav-Call-Id";
        final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";

        log.info("Henter aktive bestillinger for gruppe {}.", gruppeId);
        try {
            return webClient
                    .get()
                    .uri(sendToProd ? serviceProperties.getUrl() : devServiceProperties.getUrl(),
                            builder -> builder
                                    .path("/api/v1/bestilling/gruppe/{gruppeId}/ikkeferdig")
                                    .build(gruppeId)
                    )
                    .header(HEADER_NAV_CALL_ID, "Batch-bestilling-service")
                    .header(HEADER_NAV_CONSUMER_ID, "Batch-bestilling-service")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToFlux(Object.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException));
        } catch (WebClientResponseException.NotFound e) {
            log.warn("Fant ikke gruppe {}.", gruppeId);
            return null;
        }
    }
}
