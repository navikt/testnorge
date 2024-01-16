package no.nav.testnav.apps.tenorsearchservice.consumers.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.tenorsearchservice.domain.TenorResponse;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetTenorTestdata implements Callable<Mono<TenorResponse>> {

    private static final String TENOR_QUERY_URL = "/api/testnorge/v2/soek/freg";

    private final WebClient webClient;
    private final String query;
    private final String token;

    @Override
    public Mono<TenorResponse> call() {

        log.info("Query-parameter: {}", query);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(TENOR_QUERY_URL)
                        .queryParam("kql", URLEncoder.encode(query, StandardCharsets.US_ASCII))
                        .queryParam("nokkelinformasjon", true)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(JsonNode.class)
                .map(response -> TenorResponse.builder()
                        .status(HttpStatus.OK)
                        .data(response)
                        .build())
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(error -> Mono.just(TenorResponse.builder()
                        .status(WebClientFilter.getStatus(error))
                        .error(WebClientFilter.getMessage(error))
                        .build()));
    }
}