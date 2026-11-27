package no.nav.testnav.dollyfasade.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class PdlPersoneMiljoeGetCommand implements Callable<Mono<JsonNode>> {

    private static final String PERSON_SERVICE_URL = "/api/v2/personer/ident/{ident}";

    private final WebClient webClient;
    private final String ident;
    private final String miljoe;
    private final String token;

    @Override
    public Mono<JsonNode> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PERSON_SERVICE_URL)
                        .queryParam("miljoe", miljoe)
                        .build(ident))
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }
}
