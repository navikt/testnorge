package no.nav.registre.testnorge.batchbestillingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.batchbestillingservice.response.AktivBestillingResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetAktiveBestillingerCommand implements Callable<Flux<AktivBestillingResponse>> {

    private final WebClient webClient;
    private final String token;
    private final Long gruppeId;

    @Override
    public Flux<AktivBestillingResponse> call() throws Exception {
        log.info("Henter aktive bestillinger for gruppe {}.", gruppeId);
        return webClient
                .get()
                .uri(builder -> builder
                        .path("/api/v1/bestilling/gruppe/{gruppeId}/ikkeferdig")
                        .build(gruppeId)
                )
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(AktivBestillingResponse.class)
                .onErrorResume(WebClientResponseException.NotFound.class::isInstance,
                        throwable -> {
                            log.warn("Fant ikke gruppe {}.", gruppeId);
                            return Mono.empty();
                        })
                .retryWhen(WebClientError.is5xxException());
    }
}
