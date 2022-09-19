package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class SletteTpForholdCommand implements Callable<Flux<PensjonforvalterResponse>> {

    private static final String PENSJON_TP_PERSON_FORHOLD_URL = "/api/v1/tp/person/forhold";

    private final WebClient webClient;
    private final String ident;
    private final Set<String> miljoer;
    private final String token;


    public Flux<PensjonforvalterResponse> call() {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_TP_PERSON_FORHOLD_URL)
                        .queryParam("miljoer", String.join(",", miljoer))
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header("pid", ident)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .doOnError(Exception.class, error -> log.error(error.getMessage(), error))
                .onErrorResume(Exception.class, error -> Mono.empty());
    }
}
