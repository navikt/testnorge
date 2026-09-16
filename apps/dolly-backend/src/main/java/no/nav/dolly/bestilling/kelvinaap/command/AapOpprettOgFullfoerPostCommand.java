package no.nav.dolly.bestilling.kelvinaap.command;

import io.swagger.v3.core.util.Json;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.dolly.bestilling.kelvinaap.domain.AapOpprettRequest;
import no.nav.dolly.bestilling.kelvinaap.domain.AapOpprettResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class AapOpprettOgFullfoerPostCommand implements Callable<Mono<AapOpprettResponse>> {

    private static final String AAP_OPPRETT_URL = "/kelvin-aap/api/test/opprettOgFullfoerBehandling";

    private final WebClient webClient;
    private final AapOpprettRequest aapOpprettRequest;
    private final String token;

    @Override
    public Mono<AapOpprettResponse> call() {

        log.info("Oppretter AAP: {}", Json.pretty(aapOpprettRequest));

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(AAP_OPPRETT_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromValue(aapOpprettRequest))
                .retrieve()
                .bodyToMono(AapOpprettResponse.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> {
                    val feilmelding = WebClientError.describe(error);
                    return Mono.just(AapOpprettResponse.builder()
                            .error(feilmelding.getMessage())
                            .status(feilmelding.getStatus())
                            .build());
                });
    }
}