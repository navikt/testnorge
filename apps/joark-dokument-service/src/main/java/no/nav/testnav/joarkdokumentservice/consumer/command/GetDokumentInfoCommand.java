package no.nav.testnav.joarkdokumentservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.joarkdokumentservice.consumer.dto.Response;
import no.nav.testnav.joarkdokumentservice.util.WebClientFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.Callable;

@RequiredArgsConstructor
public class GetDokumentInfoCommand implements Callable<Mono<Response>> {

    private final WebClient webClient;
    private final String token;
    private final Integer journalpostId;
    private final String miljo;


    @Override
    public Mono<Response> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path("/{miljo}/graphql")
                        .build(miljo)
                )
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .body(BodyInserters.fromValue(
                        GraphQLRequest.builder()
                                .query(GraphQLRequest.getQueryFromFile("schema/safquery-journalpost.graphql"))
                                .variables(Map.of("journalpostId", journalpostId))
                                .build()
                ))
                .retrieve()
                .bodyToMono(Response.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }
}
