package no.nav.dolly.bestilling.oppfoelgingsvedtak14a.command;

import lombok.RequiredArgsConstructor;
import lombok.val;
import no.nav.dolly.bestilling.oppfoelgingsvedtak14a.dto.Oppfoelgingsvedtak14aRequestDTO;
import no.nav.dolly.bestilling.oppfoelgingsvedtak14a.dto.ResponseStatusDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class CreateOppfoelgingsvedtak14aCommand implements Callable<Mono<ResponseStatusDTO>> {

    private static final String OPPFOELGINGSVEDTAK14A_URL = "/oppfoelgingsvedtak14a/veilarbvedtaksstotte/api/v1/test/vedtak";

    private final WebClient webClient;
    private final Oppfoelgingsvedtak14aRequestDTO request;
    private final String token;

    @Override
    public Mono<ResponseStatusDTO> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(OPPFOELGINGSVEDTAK14A_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(request)
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
