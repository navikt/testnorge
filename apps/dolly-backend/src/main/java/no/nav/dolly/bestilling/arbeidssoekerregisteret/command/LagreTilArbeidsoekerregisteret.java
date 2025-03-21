package no.nav.dolly.bestilling.arbeidssoekerregisteret.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssoekerregisteretRequest;
import no.nav.dolly.bestilling.arbeidssoekerregisteret.dto.ArbeidssoekerregisteretResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> ArbeidssoekerregisteretResponse.of(WebClientError.describe(throwable)));
    }

}
