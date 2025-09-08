package no.nav.dolly.bestilling.tpsmessagingservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class MiljoerGetCommand implements Callable<Mono<List<String>>> {

    private static final String MILJOER_URL = "/api/v1/miljoer";

    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<List<String>> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder.path(MILJOER_URL).build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToMono(String[].class)
                .map(Arrays::asList)
                .doOnError(WebClientError.logTo(log))
                .cache(Duration.ofHours(8));
    }

}
