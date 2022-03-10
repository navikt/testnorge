package no.nav.registre.frikort.consumer.rs.command;

import lombok.AllArgsConstructor;
import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import no.nav.testnav.libs.servletcore.util.WebClientFilter;
import org.apache.http.HttpHeaders;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@AllArgsConstructor
public class PostSyntFrikortMeldingerCommand implements Callable<Map<String, List<SyntFrikortResponse>>> {

    private final Map<String, Integer> request;
    private final String token;
    private final WebClient webClient;

    private static final ParameterizedTypeReference<Map<String, List<SyntFrikortResponse>>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<Map<String, Integer>> REQUEST_TYPE = new ParameterizedTypeReference<>() {
    };

    @Override
    public Map<String, List<SyntFrikortResponse>> call() {
        return webClient.post()
                .uri(builder ->
                        builder.path("/api/v1/generate/frikort").build()
                )
                .header("Authorization", "Bearer " + token)
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(BodyInserters.fromPublisher(Mono.just(request), REQUEST_TYPE))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }
}
