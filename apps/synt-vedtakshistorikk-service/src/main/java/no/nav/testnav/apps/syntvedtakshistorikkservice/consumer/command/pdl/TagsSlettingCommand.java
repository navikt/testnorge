package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pdl;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.WebClientFilter;
import no.nav.testnav.apps.syntvedtakshistorikkservice.domain.Tags;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
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

    public Mono<ResponseEntity<JsonNode>> call() {
        log.info("Sletter tag(s) på ident(er)");
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path( "/pdl-testdata/api/v1/bestilling/tags")
                        .queryParam(IDENTS_QUERY, identer)
                        .queryParam(TAGS_QUERY, tags)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .toEntity(JsonNode.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
