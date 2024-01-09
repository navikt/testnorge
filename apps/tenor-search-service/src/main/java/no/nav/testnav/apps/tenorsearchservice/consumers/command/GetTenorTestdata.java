package no.nav.testnav.apps.tenorsearchservice.consumers.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetTenorTestdata implements Callable<Mono<JsonNode>> {

    private static final String TENOR_QUERY_URL = "/api/testnorge/v2/soek/freg";

    private final WebClient webClient;
    private final String query;
    private final String token;
    @Override
    public Mono<JsonNode> call() {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path(TENOR_QUERY_URL)
                        .queryParam("kql",query)
                        .queryParam("nokkelinformasjon",true)
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(JsonNode.class);
    }
}
