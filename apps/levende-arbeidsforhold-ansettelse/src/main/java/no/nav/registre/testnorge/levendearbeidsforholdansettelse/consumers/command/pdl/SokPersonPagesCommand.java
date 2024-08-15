package no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.pdl;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.GraphqlVariables;
import no.nav.registre.testnorge.levendearbeidsforholdansettelse.provider.PdlMiljoer;
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

import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.command.pdl.TemaGrunnlag.GEN;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.CommonKeysAndUtils.DOLLY;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.pdl.PdlHeaders.HEADER_NAV_CALL_ID;
import static no.nav.registre.testnorge.levendearbeidsforholdansettelse.consumers.PdlConsumer.hentQueryResource;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class SokPersonPagesCommand implements Callable<Mono<JsonNode>> {

    private static final String TEMA = "Tema";
    private static final String GRAPHQL_URL = "/graphql";
    private static final String PDL_API_URL = "/pdl-api";
    private static final String SOK_PERSON_QUERY = "pdl/sokPersonPages.graphql";

    private final WebClient webClient;
    private final GraphqlVariables.Paging paging;
    private final GraphqlVariables.Criteria criteria;
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
                        .fromValue(new GraphQLRequest(hentQueryResource(SOK_PERSON_QUERY),
                                Map.of("paging", paging, "criteria", criteria))))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}

