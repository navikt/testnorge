package no.nav.registre.testnorge.personsearchservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.personsearchservice.model.SearchResponse;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.concurrent.Callable;

import static org.apache.http.HttpHeaders.AUTHORIZATION;
import static org.apache.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;


@RequiredArgsConstructor
public class ElasticSearchCommand implements Callable<Flux<SearchResponse>> {

    private static final String SEARCH_URL = "/pdl-elastic/{index}/_search";

    private final WebClient webClient;
    private final String index;
    private final String token;
    private final Object body;

    private static boolean is5xxException(Throwable throwable) {

        return throwable instanceof WebClientResponseException wce &&
                wce.getStatusCode().is5xxServerError();
    }

    @Override
    public Flux<SearchResponse> call() {

        return webClient.post()
                .uri(uriBuilder -> uriBuilder
                        .path(SEARCH_URL)
                        .build(index))
                .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .header(AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(body))
                .retrieve()
                .bodyToFlux(SearchResponse.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(ElasticSearchCommand::is5xxException));
    }
}
