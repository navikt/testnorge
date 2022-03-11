package no.nav.dolly.consumer.nom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.NomProxyProperties;
import no.nav.dolly.consumer.graphql.GraphQLRequest;
import no.nav.dolly.exceptions.DollyFunctionalException;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.dolly.util.WebClientFilter;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

import static no.nav.dolly.consumer.graphql.GraphQLRequest.getQueryFromFile;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class NomConsumer {

    private static final String GRAPHQL_URL = "/graphql";
    private static final String PERSON_IDENT_QUERY = "nom/nom-personident-query.graphql";
    private static final String PERSON_HENT_ALLE_QUERY = "nom/nom-hent-alle-query.graphql";
    private static final String NOM_OPPRETT_MUTATION = "nom/nom-opprett-mutation.graphql";

    private final WebClient webClient;
    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;

    public NomConsumer(TokenExchange tokenService, NomProxyProperties serverProperties, ObjectMapper objectMapper) {
        this.tokenService = tokenService;
        this.serviceProperties = serverProperties;
        this.webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "nom_getPersonIdent" })
    public JsonNode getNomPersonMedPersonIdent(String ident) {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(GRAPHQL_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(getQueryFromFile(PERSON_IDENT_QUERY),
                                Map.of("personIdent", ident))))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnError(throwable -> {
                    throw new DollyFunctionalException("Klarte ikke å hente nom ident fra nom-api", throwable);
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException))
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "nom_getAlleNomRessurser" })
    public Mono<JsonNode> hentAlleNomRessurser() {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(GRAPHQL_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(getQueryFromFile(PERSON_HENT_ALLE_QUERY), null)))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnError(throwable -> {
                    throw new DollyFunctionalException("Klarte ikke å hente komplett nom ident fra nom-api", throwable);
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException));
    }

    @Timed(name = "providers", tags = { "operation", "nom_opprettPerson" })
    public void opprettNomPerson(String ident) {

        webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(GRAPHQL_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(getQueryFromFile(NOM_OPPRETT_MUTATION),
                                Map.of("personIdent", ident))))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .doOnError(throwable -> {
                    throw new DollyFunctionalException("Klarte ikke å opprette nom ident i nom-api", throwable);
                })
                .retryWhen(Retry.backoff(3, Duration.ofSeconds(5))
                        .filter(WebClientFilter::is5xxException)).block();
    }


    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

}