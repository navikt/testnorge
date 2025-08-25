package no.nav.dolly.bestilling.tagshendelseslager.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class TagsHenteCommand implements Callable<Mono<JsonNode>> {

    private static final String PDL_TAGS_URL = "/api/v1/bestilling/tags";
    private static final String PDL_TESTDATA = "/pdl-testdata";
    private static final String PERSONIDENT = "Nav-Personident";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Mono<JsonNode> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_TESTDATA)
                        .path(PDL_TAGS_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .header(PERSONIDENT, ident)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .retryWhen(WebClientError.is5xxException());
    }
}
