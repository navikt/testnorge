package no.nav.dolly.bestilling.pensjonforvalter.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class HentPoppInntekterCommand implements Callable<Mono<JsonNode>> {

    private static final String FNR_PARAM = "fnr";
    private static final String MILJO_HEADER = "miljo";
    private static final String POPP_INNTEKT_URL = "/api/v1/inntekt";

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final String miljoe;
    private final String callId;

    public Mono<JsonNode> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(POPP_INNTEKT_URL)
                        .queryParam(FNR_PARAM, ident)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(MILJO_HEADER, miljoe)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(WebClientError.is5xxException());
    }

}
