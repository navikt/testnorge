package no.nav.testnav.altinn3tilgangservice.consumer.altinn.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.concurrent.Callable;

@Slf4j
@RequiredArgsConstructor
public class GetExchangeTokenCommand implements Callable<Mono<String>> {

    private static final String ALTINN_URL = "/authentication/api/v1/exchange/maskinporten";

    private final WebClient webClient;
    private final String token;

    @Override
    public Mono<String> call() {

        return webClient
                .get()
                .uri(builder -> builder.path(ALTINN_URL)
                        .build()
                )
                .headers(WebClientHeader.bearer(token))
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(WebClientError.logTo(log))
                .doOnSuccess(response -> log.info("Exchange token hentet fra Altinn"));
    }
}
