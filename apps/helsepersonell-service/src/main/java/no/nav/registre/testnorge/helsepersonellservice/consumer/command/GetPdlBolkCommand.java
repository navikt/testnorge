package no.nav.registre.testnorge.helsepersonellservice.consumer.command;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.helsepersonellservice.consumer.request.GraphQLRequest;
import no.nav.registre.testnorge.helsepersonellservice.domain.PdlPersonBolk;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static java.lang.String.format;
import static no.nav.registre.testnorge.helsepersonellservice.util.Headers.CONSUMER_ID;
import static no.nav.registre.testnorge.helsepersonellservice.util.Headers.CALL_ID;
import static no.nav.registre.testnorge.helsepersonellservice.util.Headers.NAV_CONSUMER_ID;
import static no.nav.registre.testnorge.helsepersonellservice.util.Headers.AUTHORIZATION;


@RequiredArgsConstructor
public class GetPdlBolkCommand implements Callable<Mono<PdlPersonBolk>> {

    private static final String TEMA = "Tema";
    private static final String TEMA_GENERELL = "GEN";

    private final List<String> identer;
    private final String query;
    private final String token;
    private final WebClient webClient;

    @Override
    public Mono<PdlPersonBolk> call() {
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path("/pdl-api/graphql").build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(CONSUMER_ID, NAV_CONSUMER_ID)
                .header(CALL_ID, format("%s %s", NAV_CONSUMER_ID, UUID.randomUUID()))
                .header(TEMA, TEMA_GENERELL)
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(query, Map.of("identer", identer))))
                .retrieve()
                .bodyToMono(PdlPersonBolk.class);
    }
}
