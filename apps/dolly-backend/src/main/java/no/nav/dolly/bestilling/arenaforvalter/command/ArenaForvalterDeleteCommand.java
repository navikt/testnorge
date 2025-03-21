package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.arenaforvalter.dto.InaktiverResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Optional;
import java.util.concurrent.Callable;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class ArenaForvalterDeleteCommand implements Callable<Mono<InaktiverResponse>> {

    private static final String ARENAFORVALTER_BRUKER = "/api/v1/bruker";

    private final WebClient webClient;
    private final String ident;
    private final LocalDate stansDato;
    private final String environment;
    private final String token;

    @Override
    public Mono<InaktiverResponse> call() {
        return webClient
                .delete()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_BRUKER)
                                .queryParam("miljoe", environment)
                                .queryParam("personident", ident)
                                .queryParamIfPresent("stansDato",
                                        Optional.ofNullable(nonNull(stansDato) ? stansDato : null))
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .toBodilessEntity()
                .map(resultat -> InaktiverResponse.builder()
                        .status(HttpStatus.valueOf(resultat.getStatusCode().value()))
                        .build())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(throwable -> InaktiverResponse.of(WebClientError.describe(throwable)))
                .retryWhen(WebClientError.is5xxException());
    }

}
