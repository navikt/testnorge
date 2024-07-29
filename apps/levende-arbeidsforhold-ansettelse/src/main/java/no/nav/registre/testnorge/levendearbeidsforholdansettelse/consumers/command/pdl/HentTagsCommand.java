package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.pdl;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class HentTagsCommand implements Callable<JsonNode> {
    private final WebClient webClient;
    private final String token;
    private final String url = "/pdl-testdata/api/v1/bestilling/tags/bolk";
    private final String[] identer;
    @Override
    public JsonNode call() throws Exception {
        return webClient.get().uri(builder -> builder.path(url).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header("Nav-Personidenter" ,identer)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();

    }
}
