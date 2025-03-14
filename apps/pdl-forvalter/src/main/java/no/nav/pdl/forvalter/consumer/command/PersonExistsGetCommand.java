package no.nav.pdl.forvalter.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
@Slf4j
public class PersonExistsGetCommand implements Callable<Flux<Boolean>> {

    private static final String PERSON_URL = "/api/v1/personer/{ident}/exists";

    private final WebClient webClient;
    private final String ident;
    private final String token;

    @Override
    public Flux<Boolean> call() {
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PERSON_URL)
                        .build(ident))
                .header(AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .bodyToFlux(Boolean.class)
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .retryWhen(WebClientError.is5xxException());
    }

}
