package no.nav.testnav.endringsmeldingservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.endringsmeldingservice.consumer.dto.HentIdenterRequest;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class IdentpoolPostCommand implements Callable<Mono<List<String>>> {

    private static final String ACQUIRE_IDENTS_URL = "/api/v1/identifikator";

    private final WebClient webClient;
    private final HentIdenterRequest body;
    private final String token;

    @Override
    public Mono<List<String>> call() {
        return webClient
                .post()
                .uri(builder -> builder.path(ACQUIRE_IDENTS_URL).build())
                .body(BodyInserters.fromValue(body))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToMono(String[].class)
                .map(Arrays::asList)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) ->
                                new InternalError("Identpool: antall repeterende forsøk nådd")))
                .doOnError(WebClientFilter::logErrorMessage);
    }
}
