package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.GraphQLRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.PdlPerson;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.AUTHORIZATION;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.CONSUMER_TOKEN;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.CALL_ID;

@Slf4j
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
        try {
            return webClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(GRAPHQL_URL).build())
                    .header(AUTHORIZATION, "Bearer " + token)
                    .header(CONSUMER_TOKEN, "Bearer " + token)
                    .header(CALL_ID, "Dolly: " + UUID.randomUUID().toString())
                    .header(TEMA, TEMA_GENERELL)
                    .body(BodyInserters.fromValue(GraphQLRequest.builder()
                            .query(query)
                            .variables(Map.of("ident", ident, "historikk", true))
                            .build()))
                    .retrieve()
                    .bodyToMono(PdlPerson.class);
        } catch (Exception e) {
            log.error("Klarte ikke hente pdlperson.", e);
            return Mono.empty();
        }
    }
}
