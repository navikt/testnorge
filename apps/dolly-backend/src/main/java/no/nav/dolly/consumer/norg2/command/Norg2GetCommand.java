package no.nav.dolly.consumer.norg2.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.norg2.dto.Norg2EnhetResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class Norg2GetCommand implements Callable<Mono<Norg2EnhetResponse>> {

    private static final String NAVKONTOR_URL = "/norg2/api/v1/enhet/navkontor/{geografiskOmraade}";

    private final WebClient webClient;
    private final String geografiskOmraade;
    private final String token;

    @Override
    public Mono<Norg2EnhetResponse> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(NAVKONTOR_URL)
                        .build(geografiskOmraade))
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(Norg2EnhetResponse.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> Norg2EnhetResponse.of(WebClientError.describe(throwable)));
    }
}
