package no.nav.dolly.bestilling.arbeidsplassencv.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.arbeidsplassencv.dto.ArbeidsplassenCVStatusDTO;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
public class ArbeidsplassenDeleteCVCommand implements Callable<Mono<ArbeidsplassenCVStatusDTO>> {

    private static final String ARBEIDSPLASSEN_SAMTYKKE_URL = "/rest/samtykke";
    private static final String FNR = "fnr";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<ArbeidsplassenCVStatusDTO> call() {

        return webClient.delete().uri(
                        uriBuilder -> uriBuilder
                                .path(ARBEIDSPLASSEN_SAMTYKKE_URL)
                                .build())
                .header(FNR, ident)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .toBodilessEntity()
                .map(status -> ArbeidsplassenCVStatusDTO.builder()
                        .status(HttpStatus.valueOf(status.getStatusCode().value()))
                        .build())
                .doOnError(throwable -> !(throwable instanceof WebClientResponseException.NotFound), WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(WebClientResponseException.NotFound.class::isInstance, throwable -> Mono.empty());
    }
}