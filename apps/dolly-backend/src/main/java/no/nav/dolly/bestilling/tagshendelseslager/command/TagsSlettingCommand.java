package no.nav.dolly.bestilling.tagshendelseslager.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.Tags;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class TagsSlettingCommand implements Callable<Flux<String>> {

    private static final String PDL_TAGS_URL = "/api/v1/bestilling/tags";
    private static final String PDL_TESTDATA = "/pdl-testdata";
    private static final String IDENTS_QUERY = "personidenter";
    private static final String TAGS_QUERY = "tags";

    private final WebClient webClient;
    private final List<String> identer;
    private final List<Tags> tags;
    private final String token;

    public Flux<String> call() {
        return webClient
                .delete()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_TESTDATA)
                        .path(PDL_TAGS_URL)
                        .queryParam(IDENTS_QUERY, identer)
                        .queryParam(TAGS_QUERY, tags)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(String.class)
                .doOnError(WebClientError.logTo(log));
    }
}
