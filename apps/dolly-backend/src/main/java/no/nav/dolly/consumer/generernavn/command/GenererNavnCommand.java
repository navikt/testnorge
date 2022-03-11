package no.nav.dolly.consumer.generernavn.command;

import com.fasterxml.jackson.databind.JsonNode;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.securitycore.config.UserConstant;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.TokenXUtil.getUserJwt;

public record GenererNavnCommand(WebClient webClient,
                                 String token, Integer antall,
                                 String callId) implements Callable<Mono<ResponseEntity<JsonNode>>> {

    private static final String FIKTIVE_NAVN_URL = "/api/v1/navn";

    @Override
    public Mono<ResponseEntity<JsonNode>> call() {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(FIKTIVE_NAVN_URL)
                        .queryParam("antall", antall)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .header(UserConstant.USER_HEADER_JWT, getUserJwt())
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .toEntity(JsonNode.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
