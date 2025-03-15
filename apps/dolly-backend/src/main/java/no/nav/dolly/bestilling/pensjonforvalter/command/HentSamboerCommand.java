package no.nav.dolly.bestilling.pensjonforvalter.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonSamboerResponse;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class HentSamboerCommand implements Callable<Mono<PensjonSamboerResponse>> {
    private static final String PEN_SAMBOER_URL = "/{miljoe}/api/samboer/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;
    private final String token;

    public Mono<PensjonSamboerResponse> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PEN_SAMBOER_URL)
                        .queryParam("historikk", true)
                        .build(miljoe, ident))
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(PensjonSamboerResponse.class)
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .onErrorResume(error -> Mono.just(pensjonforvalterResponseFromError(miljoe, error)));
    }

    private static PensjonSamboerResponse pensjonforvalterResponseFromError(String miljoe, Throwable error) {

        var miljoeResponse = PensjonforvalterResponse.ResponseEnvironment.builder()
                .miljo(miljoe)
                .response(PensjonforvalterResponse.Response.builder()
                        .httpStatus(PensjonforvalterResponse.HttpStatus.builder()
                                .status(WebClientError.describe(error).getStatus().value())
                                .reasonPhrase(WebClientError.describe(error).getStatus().getReasonPhrase())
                                .build())
                        .message(WebClientError.describe(error).getMessage())
                        .path(PEN_SAMBOER_URL.replace("{miljoe}", miljoe))
                        .build())
                .build();

        return PensjonSamboerResponse.builder()
                .status(Collections.singletonList(miljoeResponse))
                .build();
    }
}