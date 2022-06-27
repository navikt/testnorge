package no.nav.dolly.bestilling.pensjonforvalter.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.pensjonforvalter.domain.LagreInntektRequest;
import no.nav.dolly.bestilling.pensjonforvalter.domain.PensjonforvalterResponse;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Set;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.*;
import static no.nav.dolly.util.CallIdUtil.generateCallId;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class GetInntekterCommand implements Callable<Mono<ResponseEntity<JsonNode>>> {

    private static final String FNR_QUERY = "fnr";
    private static final String MILJO_QUERY = "miljo";

    private static final String PENSJON_INNTEKT_URL = "/api/v1/inntekt";

    private final WebClient webClient;

    private final String token;

    private final String ident;
    private final String miljoe;

    public Mono<ResponseEntity<JsonNode>> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PENSJON_INNTEKT_URL)
                        .queryParam(FNR_QUERY, ident)
                        .queryParam(MILJO_QUERY, miljoe)
                        .build())
                .header(AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .toEntity(JsonNode.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
