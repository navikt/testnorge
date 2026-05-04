package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pdl;

import tools.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.Tags;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class TagsSlettingCommand implements Callable<Mono<ResponseEntity<JsonNode>>> {

    private static final String IDENTS_QUERY = "personidenter";
    private static final String TAGS_QUERY = "tags";

    private final WebClient webClient;
    private final List<String> identer;
    private final List<Tags> tags;
    private final String token;

    @Override
    public Mono<ResponseEntity<JsonNode>> call() {
        log.info("Sletter tag(s) på ident(er)");
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path("/pdl-testdata/api/v1/bestilling/tags")
                        .queryParam(IDENTS_QUERY, identer)
                        .queryParam(TAGS_QUERY, tags)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .toEntity(JsonNode.class)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }

}
