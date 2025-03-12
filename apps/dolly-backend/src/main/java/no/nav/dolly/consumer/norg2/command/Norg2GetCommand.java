package no.nav.dolly.consumer.norg2.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.consumer.norg2.dto.Norg2EnhetResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
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
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(Norg2EnhetResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(Norg2EnhetResponse.builder()
                        .httpStatus(WebClientFilter.getStatus(error))
                        .avvik(WebClientFilter.getMessage(error))
                        .build()))
                .retryWhen(WebClientError.is5xxException());
    }

}
