package no.nav.dolly.bestilling.henvendelse.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseResponse;
import no.nav.dolly.bestilling.henvendelse.dto.HenvendelseSamtalereferatRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class HenvendelsePostCommand implements Callable<Mono<HenvendelseResponse>> {

    private static final String HENVENDELSE_URL = "/henvendelse/api/henvendelse/ny/{type}";

    private final WebClient webClient;
    private final HenvendelseSamtalereferatRequest henvendelse;
    private final String token;

    @Override
    public Mono<HenvendelseResponse> call() {

        log.info("Lagrer henvendelse, request: {}", henvendelse);

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(HENVENDELSE_URL)
                        .queryParamIfPresent("kjedeId", Optional.ofNullable(henvendelse.getKjedeId()))
                        .build(henvendelse.getType()))
                .headers(WebClientHeader.bearer(token))
                .header("x-correlation-id", "Dolly-" + UUID.randomUUID())
                .bodyValue(henvendelse)
                .retrieve()
                .toBodilessEntity()
                .map(response ->
                        HenvendelseResponse.builder()
                                .status(HttpStatus.valueOf(response.getStatusCode().value()))
                                .type(henvendelse.getType())
                                .build())
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> {
                    var description = WebClientError.describe(throwable);
                    log.error("Lagring av data til (Salesforce) henvendelse feilet: {}", description.getMessage(), throwable);
                    return Mono.just(HenvendelseResponse.builder()
                            .status(description.getStatus())
                            .feilmelding(description.getMessage())
                            .type(henvendelse.getType())
                            .build());
                });
    }
}