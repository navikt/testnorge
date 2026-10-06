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
public class HenvendelseGetCommand implements Callable<Mono<HenvendelseResponse>> {

    private static final String HENVENDELSE_URL = "/henvendelse/henvendelseinfo/henvendelseliste/v2";

    private final WebClient webClient;
    private final String aktorid;
    private final String token;

    @Override
    public Mono<HenvendelseResponse> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(HENVENDELSE_URL)
                        .queryParam("aktorid", aktorid)
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
                    log.error("Henting av henvendelse fra (Salesforce) feilet: {}", description.getMessage(), throwable);
                    return Mono.just(HenvendelseResponse.builder()
                            .status(description.getStatus())
                            .melding(description.getMessage())
                            .build());
                });
    }
}