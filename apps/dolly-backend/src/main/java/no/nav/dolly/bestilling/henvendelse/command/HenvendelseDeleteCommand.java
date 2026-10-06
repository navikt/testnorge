package no.nav.dolly.bestilling.henvendelse.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class HenvendelseDeleteCommand implements Callable<Mono<HenvendelseResponse>> {

    private static final String HENVENDELSE_URL = "/henvendelse/meldingskjede/lukk";

    private final WebClient webClient;
    private final String kjedeId;
    private final String token;

    @Override
    public Mono<HenvendelseResponse> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(HENVENDELSE_URL)
                        .queryParam("kjedeId", kjedeId)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .header("x-correlation-id", "Dolly-" + UUID.randomUUID())
                .retrieve()
                .toBodilessEntity()
                .map(response -> HenvendelseResponse.builder()
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .build())
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    log.error("Lukking av henvendelse mot Salesforce feilet: {}", description.getMessage(), throwable);
                    return Mono.just(HenvendelseResponse.builder()
                            .status(description.getStatus())
                            .melding(description.getMessage())
                            .build());
                });
    }
}