package no.nav.pdl.forvalter.consumer.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.concurrent.Callable;

import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.PDL_BESTILLING_OPPRETT_PERSON_URL;
import static no.nav.pdl.forvalter.utils.PdlTestDataUrls.TemaGrunnlag.GEN;

@Slf4j
@RequiredArgsConstructor
public class PdlCreatePersonCommand implements Callable<JsonNode> {

    public static final String HEADER_NAV_PERSON_IDENT = "Nav-Personident";
    private static final String TEMA = "Tema";
    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public JsonNode call() {

        return webClient
                .post()
                .uri(builder -> builder.path(PDL_BESTILLING_OPPRETT_PERSON_URL)
                        .queryParam("kilde", "Dolly")
                        .build())
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(TEMA, GEN.name())
                .header(HEADER_NAV_PERSON_IDENT, ident)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
