package no.nav.testnav.joarkdokumentservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.joarkdokumentservice.consumer.dto.Response;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientHeader;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
@Slf4j
public class GetDokumentInfoCommand implements Callable<Mono<Response>> {

    private final WebClient webClient;
    private final String token;
    private final String journalpostId;
    private final String miljo;

    @Override
    public Mono<Response> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/saf/{miljo}/graphql")
                        .build(miljo))
                .headers(WebClientHeader.bearer(token))
                .body(BodyInserters.fromValue(
                        GraphQLRequest.builder()
                                .query(GraphQLRequest.getQueryFromFile("schema/safquery-journalpost.graphql"))
                                .variables(Map.of("journalpostId", journalpostId))
                                .build()))
                .retrieve()
                .bodyToMono(Response.class)
                .doOnError(throwable -> log.error("Feilet under henting av Journalpost", throwable))
                .retryWhen(WebClientError.is5xxException());
    }

}
