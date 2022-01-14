package no.nav.dolly.bestilling.pdldata.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.resultset.Tags;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class PdlDataSlettTagsCommand implements Callable<Mono<String>> {

    private static final String PDL_FORVALTER_TAGS_URL = "/api/v1/bestilling/tags";
    private static final String QUERY_TAGS = "tags";

    private final WebClient webClient;
    private final Set<Tags> tags;
    private final List<String> identer;
    private final String token;

    public Mono<String> call() {

        return webClient
                .method(HttpMethod.DELETE)
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_FORVALTER_TAGS_URL)
                        .queryParam(QUERY_TAGS, tags)
                        .build())
                .header(HttpHeaders.AUTHORIZATION, token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(identer)
                .retrieve()
                .bodyToMono(String.class)
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}
