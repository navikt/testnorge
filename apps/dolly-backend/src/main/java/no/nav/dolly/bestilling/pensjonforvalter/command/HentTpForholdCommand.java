package no.nav.dolly.bestilling.pensjonforvalter.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.web.reactive.function.client.WebClient;
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
public class HentTpForholdCommand implements Callable<Mono<JsonNode>> {

    private static final String PENSJON_TP_FORHOLD_URL = "/api/v1/tp/forhold";

    private static final String FNR_QUERY = "fnr";
    private static final String MILJO_QUERY = "miljo";

    private final WebClient webClient;

    private final String token;

    private final String ident;
    private final String miljoe;

    public Mono<JsonNode> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_TP_FORHOLD_URL)
                        .queryParam(FNR_QUERY, ident)
                        .queryParam(MILJO_QUERY, miljoe)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}