package no.nav.dolly.consumer.pdlperson;

import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_TOKEN;
import static no.nav.dolly.domain.resultset.pdlforvalter.TemaGrunnlag.GEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.http.Consts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.CharStreams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlPersonConsumer {

    private static final String TEMA = "Tema";
    private static final String GRAPHQL_URL = "/graphql";
    private static final String PREPROD_ENV = "q1";

    @Value("${fasit.environment.name}")
    private String environment;

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;

    public ResponseEntity getPdlPerson(String ident) {

        Map<String, Object> variables = new HashMap();
        variables.put("ident", ident);
        variables.put("historikk", true);

        String query = null;
        InputStream queryStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("pdlperson/pdlquery.graphql");
        try {
            Reader reader = new InputStreamReader(queryStream, Consts.UTF_8);
            query = CharStreams.toString(reader);
        } catch (IOException e) {
            log.error("Lesing av query ressurs feilet");
        }

        GraphQLRequest graphQLRequest = GraphQLRequest.builder()
                .query(query)
                .variables(variables)
                .build();

        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getPdlPerson().getUrl() + GRAPHQL_URL))
                .header(AUTHORIZATION, resolveToken())
                .header(HEADER_NAV_CONSUMER_TOKEN, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID().toString())
                .header(TEMA, GEN.name())
                .contentType(MediaType.APPLICATION_JSON)
                .body(graphQLRequest), JsonNode.class);
    }

    private String resolveToken() {
        return PREPROD_ENV.equals(environment) ? StsOidcService.getUserIdToken() : stsOidcService.getIdToken(PREPROD_ENV);
    }
}
