package no.nav.dolly.bestilling.tagshendelseslager.command;

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

@Slf4j
@RequiredArgsConstructor
public class TagsHentBolkCommand implements Callable<Mono<Map<String, List<String>>>> {

    private static final String PDL_TAGS_URL = "/api/v1/bestilling/tags/hentBolk";
    private static final String PDL_TESTDATA = "/pdl-testdata";

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    @Override
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
                .bodyToMono(new ParameterizedTypeReference<Map<String, List<String>>>(){})
                .doOnError(WebClientError.logTo(log));
    }
}