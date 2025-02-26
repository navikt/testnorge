package no.nav.dolly.opensearch.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchRequest;
import no.nav.testnav.libs.data.dollysearchservice.v1.SearchResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class DollySearchServicePostCommand implements Callable<Mono<SearchResponse>> {

    private static final String SEARCH_URL = "/api/v1/opensearch";

    private final WebClient webClient;
    private final SearchRequest request;
    private final String token;

    @Override
    public Mono<SearchResponse> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder.path(SEARCH_URL).build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .bodyValue(request)
                .retrieve()
                .bodyToMono(SearchResponse.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(error -> Mono.just(SearchResponse.builder()
                        .error(WebClientFilter.getMessage(error))
                        .build()));
    }
}
