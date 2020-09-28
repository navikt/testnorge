package no.nav.dolly.consumer.pdlperson;

import static java.util.Objects.nonNull;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_TOKEN;
import static no.nav.dolly.domain.resultset.pdlforvalter.TemaGrunnlag.GEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.http.Consts;
import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdlPersonConsumer {

    private static final String TEMA = "Tema";
    private static final String GRAPHQL_URL = "/graphql";
    private static final String PREPROD_ENV = "q1";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;

    @Timed(name = "providers", tags = { "operation", "pdl_getPerson" })
    public ResponseEntity getPdlPerson(String ident) {

        Map<String, Object> variables = new HashMap();
        variables.put("ident", ident);
        variables.put("historikk", true);

        StringBuilder query = new StringBuilder();

        try {
            InputStream inputStream = Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream("pdlperson/pdlquery.graphql");
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, Consts.UTF_8));
            String line = reader.readLine();
            do {
                query.append(line);
                query.append('\n');
            } while (nonNull(line = reader.readLine()));

            inputStream.close();
            reader.close();

        } catch (IOException e) {
            log.error("Lesing av query ressurs feilet");
        }

        GraphQLRequest graphQLRequest = GraphQLRequest.builder()
                .query(query.toString())
                .variables(variables)
                .build();

        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getPdlPerson().getUrl() + GRAPHQL_URL))
                .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CONSUMER_TOKEN, stsOidcService.getIdToken(PREPROD_ENV))
                .header(HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID().toString())
                .header(TEMA, GEN.name())
                .contentType(MediaType.APPLICATION_JSON)
                .body(graphQLRequest), JsonNode.class);
    }
}
