package no.nav.testnav.dollysearchservice.consumer.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class ElasticPutCommand implements Callable<Mono<String>> {
    private static final String ELASTIC_SETTINGS_URL = "/{index}/_settings";

    private final WebClient webClient;
    private final String username;
    private final String password;
    private final String index;
    private final JsonNode params;

    @Override
    public Mono<String> call() {

        return webClient.put()
                .uri(builder -> builder.path(ELASTIC_SETTINGS_URL)
                        .build(index))
                .body(BodyInserters.fromValue(params))
                .headers(httpHeaders -> httpHeaders.setBasicAuth(username, password))
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
