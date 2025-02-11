package no.nav.dolly.bestilling.arbeidssoekerregisteret.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssoekerregisteretRequest;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssoekerregisteretResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class LagreTilArbeidsoekerregisteret implements Callable<Mono<ArbeidssoekerregisteretResponse>> {

    private final WebClient webClient;
    private final ArbeidssoekerregisteretRequest request;
    private final String token;

    @Override
    public Mono<ArbeidssoekerregisteretResponse> call() {

        log.info("Lagrer i arbeidssøkerregisteret, ident: {}, request: {}", request.getIdentitetsnummer(), request);

        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/arbeidssoekerregistrering")
                                .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .map(response -> ArbeidssoekerregisteretResponse.builder()
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .build())
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(
                        ArbeidssoekerregisteretResponse.builder()
                                .status(WebClientFilter.getStatus(error))
                                .feilmelding(WebClientFilter.getMessage(error))
                                .build()));
    }
}
