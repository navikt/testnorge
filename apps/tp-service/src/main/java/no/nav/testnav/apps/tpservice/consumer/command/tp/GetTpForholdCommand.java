package no.nav.testnav.apps.tpservice.consumer.command.tp;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static no.nav.testnav.apps.tpservice.util.CallIdUtil.generateCallId;
import static no.nav.testnav.apps.tpservice.util.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.testnav.apps.tpservice.util.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.testnav.apps.tpservice.util.CommonKeysAndUtils.CONSUMER;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class GetTpForholdCommand implements Callable<Mono<JsonNode>> {

    private final WebClient webClient;
    private final String token;
    private final String ident;
    private final String miljoe;

    public Mono<JsonNode> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/api/v1/tp/forhold")
                        .queryParam("fnr", ident)
                        .queryParam("miljo", miljoe)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CALL_ID, generateCallId())
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .onErrorResume(throwable -> {
                    log.error(throwable.getMessage(), throwable);
                    return Mono.empty();
                });
    }
}
