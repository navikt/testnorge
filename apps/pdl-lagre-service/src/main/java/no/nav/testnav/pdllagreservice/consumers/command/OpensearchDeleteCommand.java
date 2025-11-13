package no.nav.testnav.pdllagreservice.consumers.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class OpensearchDeleteCommand implements Callable<Mono<JsonNode>> {
    private static final String ELASTIC_SETTINGS_URL = "/{index}";

    private final WebClient webClient;
    private final String username;
    private final String password;
    private final String index;

    @Override
    public Mono<JsonNode> call() {
        return webClient
                .delete()
                .uri(builder -> builder.path(ELASTIC_SETTINGS_URL)
                        .build(index))
                .headers(WebClientHeader.basic(username, password))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnError(WebClientError.logTo(log));
    }

}
