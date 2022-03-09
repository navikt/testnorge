package no.nav.dolly.consumer.nom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.config.credentials.NomProxyProperties;
import no.nav.dolly.consumer.graphql.GraphQLRequest;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.dolly.util.CheckAliveUtil;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.UUID;

import static no.nav.dolly.consumer.graphql.GraphQLRequest.getQueryFromFile;
import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class NomConsumer {

    private static final String GRAPHQL_URL = "/graphql";
    private static final String PERSON_IDENT_QUERY = "nom/nom-personident-query.graphql";
    private static final String PERSON_NAVIDENT_QUERY = "nom/nom-navident-query.graphql";
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
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID())
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(getQueryFromFile(PERSON_IDENT_QUERY),
                                Map.of("personIdent", ident))))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "nom_getKomplettPerson" })
    public JsonNode getKomplettNomPersonMedNavIdent(String navIdent) {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(GRAPHQL_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID())
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(getQueryFromFile(PERSON_NAVIDENT_QUERY),
                                Map.of("navIdent", navIdent))))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "nom_opprettPerson" })
    public ResponseEntity<JsonNode> opprettNomPerson(String ident) {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(GRAPHQL_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID())
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(getQueryFromFile(NOM_OPPRETT_MUTATION),
                                Map.of("personIdent", ident))))
                .retrieve()
                .toEntity(JsonNode.class)
                .block();
    }


    public Map<String, String> checkAlive() {
        return CheckAliveUtil.checkConsumerAlive(serviceProperties, webClient, tokenService);
    }

}