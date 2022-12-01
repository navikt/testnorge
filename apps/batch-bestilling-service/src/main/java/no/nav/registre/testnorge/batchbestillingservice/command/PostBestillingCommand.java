package no.nav.registre.testnorge.batchbestillingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.batchbestillingservice.request.RsDollyBestillingRequest;
import no.nav.testnav.libs.commands.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.Disposable;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class PostBestillingCommand implements Callable<Disposable> {
    private final WebClient webClient;
    private final String token;
    private final Long gruppeId;
    private final RsDollyBestillingRequest request;
    private final Boolean sendToProd;
    private final ServerProperties serviceProperties;
    private final ServerProperties devServiceProperties;

    @Override
    public Disposable call() {
        final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";
        final String HEADER_NAV_CALL_ID = "Nav-Call-Id";

        log.info("Sender batch bestilling til Dolly backend for gruppe {}.", gruppeId);
        try {
            return webClient.post()
                    .uri(sendToProd ? serviceProperties.getUrl() : devServiceProperties.getUrl(),
                            builder -> builder
                                    .path("/api/v1/gruppe/{gruppeId}/bestilling").build(gruppeId))
                    .header(AUTHORIZATION, "Bearer " + token)
                    .header(HEADER_NAV_CALL_ID, "Batch-bestilling-service")
                    .header(HEADER_NAV_CONSUMER_ID, "Batch-bestilling-service")
                    .bodyValue(request)
                    .retrieve()
                    .toBodilessEntity()
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .doOnError(throwable -> log.error(throwable.getMessage()))
                    .subscribe();

        } catch (WebClientResponseException.NotFound | NullPointerException e) {
            log.warn("Fant ikke gruppe {}.", gruppeId);
        }
        return null;
    }
}
