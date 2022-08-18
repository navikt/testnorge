package no.nav.testnav.apps.tpservice.consumer.rs.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

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
//                .header(HEADER_NAV_CALL_ID, generateCallId())
//                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }
}
