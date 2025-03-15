package no.nav.dolly.bestilling.brregstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_PERSON_IDENT;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class BrregDeleteCommand implements Callable<Flux<Void>> {

    private static final String ROLLEOVERSIKT_URL = "/api/v2/rolleoversikt";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Flux<Void> call() {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder.path(ROLLEOVERSIKT_URL).build())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .retrieve()
                .bodyToFlux(Void.class)
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(error -> Mono.empty());
    }

}
