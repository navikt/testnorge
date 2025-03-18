package no.nav.testnav.dollysearchservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;


@RequiredArgsConstructor
public class TagsGetCommand implements Callable<Mono<Map<String, List<String>>>> {

    private static final String PDL_TAGS_URL = "/api/v1/bestilling/tags/bolk";
    private static final String PDL_TESTDATA = "/pdl-testdata";
    private static final String PERSONIDENTER = "Nav-Personidenter";
    private static final ParameterizedTypeReference<Map<String, List<String>>> RESPONSE_TYPE = new ParameterizedTypeReference<>(){};

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    public Mono<Map<String, List<String>>> call() {

        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_TESTDATA)
                        .path(PDL_TAGS_URL)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .header(PERSONIDENTER, identer.toArray(new String[0]))
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
