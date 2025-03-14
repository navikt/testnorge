package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.command.pdl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.pdl.GraphQLRequest;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.response.pdl.PdlPersonBolk;
import no.nav.testnav.libs.reactivecore.web.WebClientError;
import no.nav.testnav.libs.reactivecore.web.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.util.Headers.*;

@RequiredArgsConstructor
@Slf4j
public class GetPdlPersonerCommand implements Callable<Mono<PdlPersonBolk>> {

    private static final String TEMA = "Tema";
    private static final String TEMA_GENERELL = "GEN";
    private static final String GRAPHQL_URL = "/pdl-api/graphql";

    private final List<String> identer;
    private final String query;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<PdlPersonBolk> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(GRAPHQL_URL).build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(CONSUMER_ID, NAV_CONSUMER_ID)
                .header(CALL_ID, NAV_CALL_ID + ": " + UUID.randomUUID())
                .header(TEMA, TEMA_GENERELL)
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(query, Map.of("identer", identer))))
                .retrieve()
                .bodyToMono(PdlPersonBolk.class)
                .doOnError(throwable -> WebClientError.log(throwable, log))
                .retryWhen(WebClientError.is5xxException());
    }

}