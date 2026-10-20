package no.nav.dolly.bestilling.oppfoelgingsvedtak14a.command;

import lombok.RequiredArgsConstructor;
import lombok.val;
import no.nav.dolly.bestilling.oppfoelgingsvedtak14a.dto.RequestDTO;
import no.nav.dolly.bestilling.oppfoelgingsvedtak14a.dto.ResponseStatusDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class StartOppfoelgingsperiodeCommand implements Callable<Mono<ResponseStatusDTO>> {

    private static final String OPPFOELGING_URL = "/oppfoelging/veilarboppfolging/api/v1/dolly/startOppfolgingsperiode";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<ResponseStatusDTO> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(OPPFOELGING_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .header("Nav-Consumer-Id", "dolly-proxy")
                .bodyValue(RequestDTO.builder()
                        .fnr(ident)
                        .build())
                .retrieve()
                .toBodilessEntity()
                .map(status -> ResponseStatusDTO.builder()
                        .status(HttpStatus.valueOf(status.getStatusCode().value()))
                        .build())
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> {
                    val feilmelding = WebClientError.describe(error);
                    return Mono.just(ResponseStatusDTO.builder()
                            .status(feilmelding.getStatus())
                            .reason(feilmelding.getMessage())
                            .build());
                });
    }
}
