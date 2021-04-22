package no.nav.pdl.forvalter.consumer.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.concurrent.Callable;

import static no.nav.pdl.forvalter.consumer.PdlTestDataUrls.PDL_BESTILLING_SLETTING_URL;

@Slf4j
@RequiredArgsConstructor
public class PdlDeletePersonCommand implements Callable<JsonNode> {

    public static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";
    private static final String TEMA = "Tema";
    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public JsonNode call() {

        try {
            return webClient
                    .delete()
                    .uri(builder -> builder.path(PDL_BESTILLING_SLETTING_URL).build())
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                    .header(TEMA, TemaGrunnlag.GEN.name())
                    .header(HEADER_NAV_PERSON_IDENT, ident)
                    .retrieve()
                    .bodyToMono(JsonNode.class)
                    .block();

        } catch (
                WebClientResponseException e) {
            log.error("Feil ved sletting av PDL-testdata: {}.", e.getResponseBodyAsString(), e);
            throw e;
        }
    }

    public enum TemaGrunnlag {GEN, PEN}
}
