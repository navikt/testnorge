package no.nav.dolly.bestilling.arbeidssoekerregisteret.command;

import lombok.RequiredArgsConstructor;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssokerregisteretRequest;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssokerregisteretResponse;
import no.nav.dolly.bestilling.dokarkiv.domain.DokarkivResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.apache.commons.lang3.BooleanUtils.isTrue;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class LagreTilArbeidsoekerregisteret implements Callable<Mono<ArbeidssokerregisteretResponse>> {

    private static final String ARBEIDSOEKERREGISTERET = "/api/v1/arbeidssoekerregistrering";

    private final WebClient webClient;
    private final ArbeidssokerregisteretRequest request;
    private final String token;

    @Override
    public Mono<ArbeidssokerregisteretResponse> call() {

        return webClient.post()
                .uri(builder ->
                        builder.path(ARBEIDSOEKERREGISTERET)
                                .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .bodyValue(request)
                .retrieve()
                .toBodilessEntity()
                .map(response -> ArbeidssokerregisteretResponse.builder()
                        .status(HttpStatus.valueOf(response.getStatusCode().value()))
                        .build())
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(error -> Mono.just(
                        ArbeidssokerregisteretResponse.builder()
                                .status(WebClientFilter.getStatus(error))
                                .feilmelding(WebClientFilter.getMessage(error))
                                .build()));
    }
}
