package no.nav.dolly.consumer.pdlperson.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.pdlperson.GraphQLRequest;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.util.CallIdUtil;
import no.nav.dolly.util.WebClientFilter;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static no.nav.dolly.consumer.pdlperson.PdlPersonConsumer.hentQueryResource;
import static no.nav.dolly.consumer.pdlperson.TemaGrunnlag.GEN;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class PdlBolkPersonGetCommand implements Callable<Flux<PdlPersonBolk>> {

    private static final String TEMA = "Tema";
    private static final String GRAPHQL_URL = "/graphql";
    private static final String PDL_API_URL = "/pdl-api";
    private static final String MULTI_PERSON_QUERY = "pdlperson/pdlbolkquery.graphql";

    private final WebClient webClient;
    private final List<String> identer;
    private final String token;

    @Override
    public Flux<PdlPersonBolk> call() {

        log.info("Request sendt til PDL for {}", String.join(", ", identer));
        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_API_URL)
                        .path(GRAPHQL_URL)
                        .build())
                .header(AUTHORIZATION, "Bearer " + token)
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_CALL_ID, CallIdUtil.generateCallId())
                .header(TEMA, GEN.name())
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(hentQueryResource(MULTI_PERSON_QUERY),
                                Map.of("identer", identer))))
                .retrieve()
                .bodyToFlux(PdlPersonBolk.class)
                .doOnError(WebClientFilter::logErrorMessage)
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .onErrorResume(throwable -> throwable instanceof WebClientResponseException.NotFound,
                        throwable -> Mono.empty());
    }
}
