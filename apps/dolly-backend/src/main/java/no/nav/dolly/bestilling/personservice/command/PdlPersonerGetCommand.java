package no.nav.dolly.bestilling.personservice.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class PdlPersonerGetCommand implements Callable<Flux<PdlPersonBolk>> {

    private static final String PERSON_SERVICE_URL = "/api/v2/personer/identer";

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    @Override
    public Flux<PdlPersonBolk> call() {
        log.info("PdlPersonerGetCommand: Starter kall for identer: {}", identer);
        return webClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path(PERSON_SERVICE_URL)
                        .queryParam("identer", identer)
                        .build())
                .headers(WebClientHeader.bearer(token))
                .retrieve()
                .bodyToFlux(PdlPersonBolk.class)
                .doOnNext(result -> log.debug("PdlPersonerGetCommand: Mottok resultat for {} personer", 
                        result.getData() != null && result.getData().getHentPersonBolk() != null 
                                ? result.getData().getHentPersonBolk().size() : 0))
                .doOnError(error -> {
                    log.error("PdlPersonerGetCommand: Feil ved kall, exceptionType={}, message={}", 
                            error.getClass().getName(), error.getMessage());
                    if (error.getCause() != null) {
                        log.error("PdlPersonerGetCommand: Caused by: exceptionType={}, message={}", 
                                error.getCause().getClass().getName(), error.getCause().getMessage());
                    }
                    log.error("PdlPersonerGetCommand: Full stacktrace:", error);
                })
                .retryWhen(WebClientError.is5xxException())
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }

}
