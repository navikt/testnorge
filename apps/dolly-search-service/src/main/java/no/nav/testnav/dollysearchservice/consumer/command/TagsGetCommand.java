package no.nav.testnav.dollysearchservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class TagsGetCommand implements Callable<Mono<Map<String, List<String>>>> {

    private static final String PDL_TAGS_URL = "/api/v1/bestilling/tags/hentBolk";
    private static final String PDL_TESTDATA = "/pdl-testdata";
    private static final ParameterizedTypeReference<Map<String, List<String>>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    public Mono<Map<String, List<String>>> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_TESTDATA)
                        .path(PDL_TAGS_URL)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .bodyValue(identer)
                .retrieve()
                .bodyToMono(RESPONSE_TYPE)
                .doOnError(WebClientError.logTo(log))
                .retryWhen(WebClientError.is5xxException());
    }
}
