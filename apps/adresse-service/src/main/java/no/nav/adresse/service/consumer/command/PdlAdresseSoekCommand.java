package no.nav.adresse.service.consumer.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import no.nav.adresse.service.dto.GraphQLRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PdlAdresseSoekCommand implements Callable<JsonNode> {

    private static final String TEMA = "Tema";

    private final WebClient webClient;
    private final GraphQLRequest query;
    private final String token;

    @SneakyThrows
    @Override
    public JsonNode call() {

        try {
            return webClient
                    .post()
                    .uri(builder -> builder.path("/pdl-api/graphql").build())
                    .body(BodyInserters.fromValue(query))
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(TEMA, TemaGrunnlag.GEN.name())
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

        } catch (
                WebClientResponseException e) {
            log.error("Feil ved henting av adressedata: {}.", e.getResponseBodyAsString());
            log.error(query.toString());
            throw e;
        }
    }

    public enum TemaGrunnlag {GEN, PEN}
}
