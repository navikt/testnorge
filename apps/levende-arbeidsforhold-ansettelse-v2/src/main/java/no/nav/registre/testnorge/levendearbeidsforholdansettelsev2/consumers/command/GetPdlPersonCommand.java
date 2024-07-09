package no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.command;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.pdl.graphql.PdlPerson;
import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.pdl.graphql.Request;
import no.nav.registre.testnorge.levendearbeidsforholdansettelsev2.consumers.pdl.graphql.PdlHeaders;
import no.nav.testnav.libs.reactivecore.utils.WebClientFilter;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

//import static no.nav.testnav.apps.personservice.consumer.v2.PdlPersonConsumer.hentQueryResource;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.Callable;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class GetPdlPersonCommand implements Callable<Mono<JsonNode>> {
    private final WebClient webClient;
    private final String url;
    private final String ident;
    private final String token;
    private final String query;
    private final String TEMA_GENERELL = "GEN";
    public static final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";
    public static final String HEADER_NAV_CALL_ID = "Nav-Call-Id";
    public static final String DOLLY = "Dolly";
    @Override
    public Mono<JsonNode> call() {
/*
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
                .doOnError(no.nav.testnav.libs.reactivecore.utils.WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());

 */
        return null;
    }


}
