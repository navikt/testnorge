package no.nav.dolly.bestilling.arenaforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class ArenaForvalterGetMiljoeCommand implements Callable<Flux<String>> {

    private static final String ARENAFORVALTER_ENVIRONMENTS = "/api/v1/miljoe";

    private final WebClient webClient;
    private final String token;

    @Override
    public Flux<String> call() {
        return webClient
                .get()
                .uri(
                        uriBuilder -> uriBuilder
                                .path(ARENAFORVALTER_ENVIRONMENTS)
                                .build())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToMono(String[].class)
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .flatMapIterable(miljoer -> Arrays.stream(miljoer).toList())
                .retryWhen(WebClientError.is5xxException());
    }

}
