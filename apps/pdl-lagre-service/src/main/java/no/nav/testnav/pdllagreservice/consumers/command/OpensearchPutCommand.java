package no.nav.testnav.pdllagreservice.consumers.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class OpensearchPutCommand implements Callable<Mono<String>> {

    private static final String OPENSEARCH_INDEX_URL = "/{index}";

    private final WebClient webClient;
    private final Optional<String> segment;
    private final String username;
    private final String password;
    private final String index;
    private final JsonNode params;

    @Override
    public Mono<String> call() {

        return webClient
                .put()
                .uri(uriBuilder -> {
                    var builder = uriBuilder.path(OPENSEARCH_INDEX_URL);
                    if (segment.isPresent()) {
                        builder = builder.pathSegment(segment.get());
                    }
                    return builder.build(index);
                })
                .body(BodyInserters.fromValue(params))
                .headers(WebClientHeader.basic(username, password))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(WebClientError.logTo(log));
    }
}
