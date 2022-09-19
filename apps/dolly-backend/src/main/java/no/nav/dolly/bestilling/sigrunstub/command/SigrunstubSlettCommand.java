package no.nav.dolly.bestilling.sigrunstub.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.CallIdUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class SigrunstubSlettCommand implements Callable<Flux<String>> {

    private static final String CONSUMER = "Dolly";
    private static final String SIGRUNSTUB_DELETE_URL = "/api/v1/slett";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    public Flux<String> call() {

        return webClient.delete().uri(uriBuilder -> uriBuilder
                        .path(SIGRUNSTUB_DELETE_URL)
                        .build())
                .header(HEADER_NAV_CALL_ID, CallIdUtil.generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header("personidentifikator", ident)
                .retrieve()
                .bodyToFlux(String.class)
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Flux.empty())
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
