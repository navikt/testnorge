package no.nav.dolly.bestilling.arbeidsplassencv.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.ArbeidsplassenCVStatusDTO;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class ArbeidsplassenGetPersonCommand implements Callable<Mono<ArbeidsplassenCVStatusDTO>> {

    private static final String ARBEIDSPLASSEN_PERSON_URL = "/rest/v2/person";
    private static final String FNR = "fnr";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<ArbeidsplassenCVStatusDTO> call() {

        return webClient.get().uri(
                        uriBuilder -> uriBuilder
                                .path(ARBEIDSPLASSEN_PERSON_URL)
                                .build())
                .header(FNR, ident)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(json -> ArbeidsplassenCVStatusDTO.builder()
                        .jsonNode(json)
                        .status(HttpStatus.OK)
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> Mono.just(ArbeidsplassenCVStatusDTO.builder()
                        .feilmelding(WebClientFilter.getMessage(throwable))
                        .status(WebClientFilter.getStatus(throwable))
                        .build()));
    }
}
