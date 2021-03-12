package no.nav.dolly.consumer.pdlperson;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;
import org.apache.http.Consts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_TOKEN;
import static no.nav.dolly.domain.resultset.pdlforvalter.TemaGrunnlag.GEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Service
public class PdlPersonConsumer {

    private static final String TEMA = "Tema";
    private static final String GRAPHQL_URL = "/graphql";
    private static final String PREPROD_ENV = "q1";
    private static final String SINGLE_PERSON_QUERY = "pdlperson/pdlquery.graphql";
    private static final String MULTI_PERSON_QUERY = "pdlperson/pdlbolkquery.graphql";

    private StsOidcService stsOidcService;
    private WebClient webClient;

    public PdlPersonConsumer(ProvidersProps providersProps, StsOidcService stsOidcService) {

        this.stsOidcService = stsOidcService;
        webClient = WebClient.builder()
                .baseUrl(providersProps.getPdlPerson().getUrl())
                .build();
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

    @Timed(name = "providers", tags = {"operation", "pdl_getPerson"})
    public JsonNode getPdlPerson(String ident) {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(GRAPHQL_URL).build())
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID().toString())
                .header(TEMA, GEN.name())
                .body(BodyInserters.fromValue(GraphQLRequest.builder()
                        .query(getQueryFromFile(SINGLE_PERSON_QUERY))
                        .variables(Map.of("ident", ident, "historikk", true))
                        .build()))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }

    @Timed(name = "providers", tags = {"operation", "pdl_getPersoner"})
    public JsonNode getPdlPersoner(List<String> identer) {

        return webClient
                .post()
                .uri(uriBuilder -> uriBuilder.path(GRAPHQL_URL).build())
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID().toString())
                .header(TEMA, GEN.name())
                .body(BodyInserters.fromValue(GraphQLRequest.builder()
                        .query(getQueryFromFile(MULTI_PERSON_QUERY))
                        .variables(Map.of("identer", identer))
                        .build()))
                .retrieve()
                .bodyToMono(JsonNode.class)
                .block();
    }
}
