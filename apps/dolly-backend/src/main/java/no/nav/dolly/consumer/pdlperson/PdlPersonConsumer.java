package no.nav.dolly.consumer.pdlperson;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.dolly.config.credentials.PdlProxyProperties;
import no.nav.dolly.domain.PdlPersonBolk;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.security.config.NaisServerProperties;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.apache.http.Consts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static no.nav.dolly.domain.CommonKeysAndUtils.CONSUMER;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_ID;
import static no.nav.dolly.domain.resultset.pdlforvalter.TemaGrunnlag.GEN;
import static no.nav.dolly.util.JacksonExchangeStrategyUtil.getJacksonStrategy;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class PdlPersonConsumer {

    private static final String TEMA = "Tema";
    private static final String GRAPHQL_URL = "/graphql";
    private static final String PDL_API_URL = "/pdl-api";
    private static final String SINGLE_PERSON_QUERY = "pdlperson/pdlquery.graphql";
    private static final String MULTI_PERSON_QUERY = "pdlperson/pdlbolkquery.graphql";

    private final TokenExchange tokenService;
    private final NaisServerProperties serviceProperties;
    private final WebClient webClient;

    public PdlPersonConsumer(TokenExchange tokenService, PdlProxyProperties serverProperties, ObjectMapper objectMapper) {

        this.serviceProperties = serverProperties;
        this.tokenService = tokenService;
        webClient = WebClient.builder()
                .baseUrl(serverProperties.getUrl())
                .exchangeStrategies(getJacksonStrategy(objectMapper))
                .build();
    }

    @Timed(name = "providers", tags = { "operation", "pdl_getPerson" })
    public JsonNode getPdlPerson(String ident) {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_API_URL)
                        .path(GRAPHQL_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID())
                .header(TEMA, GEN.name())
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(getQueryFromFile(SINGLE_PERSON_QUERY),
                                Map.of("ident", ident, "historikk", true))))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    @Timed(name = "providers", tags = { "operation", "pdl_getPersoner" })
    public PdlPersonBolk getPdlPersoner(List<String> identer) {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder
                        .path(PDL_API_URL)
                        .path(GRAPHQL_URL)
                        .build())
                .header(AUTHORIZATION, serviceProperties.getAccessToken(tokenService))
                .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                .header(HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID())
                .header(TEMA, GEN.name())
                .body(BodyInserters
                        .fromValue(new GraphQLRequest(getQueryFromFile(MULTI_PERSON_QUERY),
                                Map.of("identer", identer))))
                .retrieve()
                .bodyToMono(PdlPersonBolk.class)
                .block();
    }

    public Map<String, String> checkAlive() {
        try {
            return Map.of(serviceProperties.getName() + PDL_API_URL, serviceProperties.checkIsAlive(webClient, serviceProperties.getAccessToken(tokenService)));
        } catch (SecurityException | WebClientResponseException ex) {
            log.error("{} feilet mot URL: {}", serviceProperties.getName(), serviceProperties.getUrl(), ex);
            return Map.of(serviceProperties.getName(), String.format("%s, URL: %s", ex.getMessage(), serviceProperties.getUrl()));
        }
    }

    private static String getQueryFromFile(String pathResource) {

        val resource = new ClassPathResource(pathResource);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), Consts.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            log.error("Lesing av query ressurs {} feilet", pathResource, e);
            return null;
        }
    }
}
