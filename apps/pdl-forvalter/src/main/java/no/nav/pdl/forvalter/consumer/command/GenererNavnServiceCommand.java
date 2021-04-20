package no.nav.pdl.forvalter.consumer.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GenererNavnServiceCommand implements Callable<JsonNode> {

    private final WebClient webClient;
    private final String url;
    private final Object body;
    private final String token;

    @Override
    public JsonNode call() {

        try {
            return webClient
                    .post()
                    .uri(builder -> builder.path("/pdl-testdata/" + url).build())
                    .body(BodyInserters.fromValue(body))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

        } catch (
                WebClientResponseException e) {
            log.error("Feil ved skriving av PDL-testdata: {}.", e.getResponseBodyAsString());
            log.error(body.toString());
            throw e;
        }
    }
}
