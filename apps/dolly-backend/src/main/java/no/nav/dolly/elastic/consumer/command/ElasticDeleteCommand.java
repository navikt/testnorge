package no.nav.dolly.elastic.consumer.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class ElasticDeleteCommand implements Callable<Mono<JsonNode>> {
    private static final String ELASTIC_SETTINGS_URL = "/{index}";

    private final WebClient webClient;
    private final String username;
    private final String password;
    private final String index;

    @Override
    public Mono<JsonNode> call() {

        return webClient.delete()
                .uri(builder -> builder.path(ELASTIC_SETTINGS_URL)
                        .build(index))
                .headers(httpHeaders -> httpHeaders.setBasicAuth(username, password))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
