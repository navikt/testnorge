package no.nav.registre.testnorge.arena.consumer.rs.command.pdl;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.arena.consumer.rs.request.pdl.GraphQLRequest;
import no.nav.registre.testnorge.arena.consumer.rs.response.pdl.PdlPerson;
import no.nav.registre.testnorge.arena.util.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.AUTHORIZATION;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CALL_ID;
import static no.nav.registre.testnorge.arena.consumer.rs.util.Headers.CONSUMER_TOKEN;

@Slf4j
public class GetPdlPersonCommand implements Callable<PdlPerson> {

    private static final String TEMA = "Tema";
    private static final String TEMA_GENERELL = "GEN";
    private static final String GRAPHQL_URL = "/graphql";

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
    public PdlPerson call() {
        try {
            return webClient
                    .post()
                    .uri(uriBuilder -> uriBuilder.path(GRAPHQL_URL).build())
                    .header(AUTHORIZATION, "Bearer " + token)
                    .header(CONSUMER_TOKEN, "Bearer " + token)
                    .header(CALL_ID, "Dolly: " + UUID.randomUUID())
                    .header(TEMA, TEMA_GENERELL)
                    .body(BodyInserters.fromValue(GraphQLRequest.builder()
                            .query(query)
                            .variables(Map.of("ident", ident, "historikk", true))
                            .build()))
                    .retrieve()
                    .bodyToMono(PdlPerson.class)
                    .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                            .filter(WebClientFilter::is5xxException))
                    .block();
        } catch (Exception e) {
            log.error("Klarte ikke hente pdlperson.", e);
            return null;
        }
    }
}
