package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pdl;

import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pdl.GraphQLRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl.PdlPerson;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.*;

@Slf4j
public class GetPdlPersonCommand implements Callable<Mono<PdlPerson>> {

    private static final String TEMA = "Tema";
    private static final String TEMA_GENERELL = "GEN";
    private static final String GRAPHQL_URL = "/pdl-api/graphql";

    private final WebClient webClient;
    private final String ident;
    private final String query;
    private final String token;

    public GetPdlPersonCommand(String ident, String query, String token, WebClient webClient) {
        this.ident = ident;
        this.query = query;
        this.token = token;
        this.webClient = webClient;
    }

    @Override
    public Mono<PdlPerson> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(GRAPHQL_URL).build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(CONSUMER_ID, NAV_CONSUMER_ID)
                .header(CALL_ID, NAV_CALL_ID + ": " + UUID.randomUUID())
                .header(TEMA, TEMA_GENERELL)
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(query, Map.of("ident", ident, "historikk", true))))
                .retrieve()
                .bodyToMono(PdlPerson.class)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));

    }
}