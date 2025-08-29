package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Set;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

@RequiredArgsConstructor
@Slf4j
public class SletteTpForholdCommand implements Callable<Flux<PensjonforvalterResponse>> {

    private static final String PENSJON_TP_PERSON_FORHOLD_URL = "/api/v1/tp/person/forhold";

    private final WebClient webClient;
    private final String ident;
    private final Set<String> miljoer;
    private final String token;

    @Override
    public Flux<PensjonforvalterResponse> call() {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_TP_PERSON_FORHOLD_URL)
                        .queryParam("miljoer", String.join(",", miljoer))
                        .build())
                .headers(WebClientHeader.bearer(token))
                .headers(WebClientHeader.jwt(getUserJwt()))
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header("pid", ident)
                .retrieve()
                .bodyToFlux(PensjonforvalterResponse.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log))
                .onErrorResume(Exception.class, error -> Mono.empty());
    }

}