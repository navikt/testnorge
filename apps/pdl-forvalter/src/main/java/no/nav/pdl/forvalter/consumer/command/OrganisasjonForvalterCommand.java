package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.databind.JsonNode;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class OrganisasjonForvalterCommand implements Callable<Mono<JsonNode>> {

    private final WebClient webClient;
    private final String url;
    private final String query;
    private final String token;

    @Override
    public Mono<JsonNode> call() {
        return webClient
                .get()
                .uri(builder -> builder.path(url).query(query).build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .retryWhen(WebClientError.is5xxException());
    }
}