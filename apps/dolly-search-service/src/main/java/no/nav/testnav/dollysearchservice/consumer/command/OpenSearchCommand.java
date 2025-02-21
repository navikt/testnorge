package no.nav.testnav.dollysearchservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.dollysearchservice.dto.SearchResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RequiredArgsConstructor
public class OpenSearchCommand implements Callable<Mono<SearchResponse>> {

    private static final String SEARCH_URL = "/pdl-elastic/{index}/_search";

    private final WebClient webClient;
    private final String index;
    private final String token;
    private final Object body;

    @Override
    public Mono<SearchResponse> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(SEARCH_URL)
                        .build(index))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToMono(SearchResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> Mono.just(SearchResponse.builder()
                                .status(WebClientFilter.getStatus(throwable))
                                .error(WebClientFilter.getMessage(throwable))
                        .build()));
    }
}
