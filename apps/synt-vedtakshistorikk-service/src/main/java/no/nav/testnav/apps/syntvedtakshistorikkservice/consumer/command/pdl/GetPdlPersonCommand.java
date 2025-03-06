package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pdl;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pdl.GraphQLRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl.PdlPerson;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.*;

@RequiredArgsConstructor
public class GetPdlPersonCommand implements Callable<Mono<PdlPerson>> {

    private static final String TEMA = "Tema";
    private static final String TEMA_GENERELL = "GEN";
    private static final String GRAPHQL_URL = "/pdl-api/graphql";

    private final String ident;
    private final String query;
    private final String token;
    private final WebClient webClient;

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
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));

    }
}