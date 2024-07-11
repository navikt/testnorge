package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.GraphQLRequest;
import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.provider.PdlMiljoer;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.TemaGrunnlag.GEN;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.domain.CommonKeysAndUtils.DOLLY;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.pdl.graphql.PdlHeaders.HEADER_NAV_CALL_ID;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.PdlConsumer.hentQueryResource;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.TemaGrunnlag.GEN;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.JacksonExchangeStrategyUtil.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class PdlPersonGetCommand implements Callable<Mono<JsonNode>> {

    private static final String TEMA = "Tema";
    private static final String GRAPHQL_URL = "/graphql";
    private static final String PDL_API_URL = "/pdl-api";
    private static final String SINGLE_PERSON_QUERY = "pdl/pdlPerson2Query.graphql";

    private final WebClient webClient;
    private final String ident;
    private final String token;
    private final PdlMiljoer pdlMiljoe;

    @Override
    public Mono<JsonNode> call() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_API_URL)
                        .path(pdlMiljoe.equals(PdlMiljoer.Q2) ? "" : "-" + pdlMiljoe.name().toLowerCase())
                        .path(GRAPHQL_URL)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CONSUMER_ID, DOLLY)
                .header(HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID())
                .header(TEMA, GEN.name())
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(hentQueryResource(SINGLE_PERSON_QUERY),
                                Map.of("ident", ident, "historikk", true))))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}
