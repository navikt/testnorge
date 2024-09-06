package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonVedtakResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class PensjonHentVedtakCommand implements Callable<Flux<PensjonVedtakResponse>> {

    private static final String VEDTAK_URL = "/api/v2/vedtak";

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;
    private final String token;

    @Override
    public Flux<PensjonVedtakResponse> call() {

        var callId = generateCallId();
        log.info("Pensjon hente vedtak for ident {}, miljoe {}, callId {}", ident, miljoe, callId);

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(VEDTAK_URL)
                        .queryParam("miljo", miljoe)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header("fnr", ident)
                .retrieve()
                .bodyToFlux(PensjonVedtakResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(WebClientFilter::logErrorMessage)
                .onErrorResume(Exception.class, error -> Mono.empty());
    }
}
