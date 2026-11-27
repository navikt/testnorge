package no.nav.testnav.dollyfasade.consumers.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetTenorTestdataDomainCommand implements Callable<Mono<List<String>>> {

    private static final String TENOR_QUERY_URL = "/api/v1/tenor/testdata/domain";

    private final WebClient webClient;
    private final String lookup;
    private final String token;

    @Override
    public Mono<List<String>> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(TENOR_QUERY_URL)
                        .queryParam("lookup", lookup)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {
                })
                .retryWhen(WebClientError.is5xxException())
                .doOnError(WebClientError.logTo(log));
    }
}