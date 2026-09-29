package no.nav.dolly.bestilling.kelvinaap.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.dolly.bestilling.kelvinaap.domain.AapStatusRequest;
import no.nav.dolly.bestilling.kelvinaap.domain.AapStatusResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class AapBehandlingStatusPostCommand implements Callable<Mono<AapStatusResponse>> {

    private static final String AAP_STATUS_URL = "/kelvin-aap/api/test/behandlingStatus";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<AapStatusResponse> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(AAP_STATUS_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromValue(AapStatusRequest.builder()
                        .ident(ident)
                        .build()))
                .retrieve()
                .bodyToMono(AapStatusResponse.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> {
                    val feilmelding = WebClientError.describe(error);
                    return Mono.just(AapStatusResponse.builder()
                            .status(feilmelding.getStatus())
                            .error(feilmelding.getMessage())
                            .build());
                });
    }
}