package no.nav.dolly.consumer.pdlperson;

import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeysAndUtils.HEADER_NAV_CONSUMER_TOKEN;
import static no.nav.dolly.domain.resultset.pdlforvalter.TemaGrunnlag.GEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.http.Consts;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
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

        return restTemplate.exchange(RequestEntity.post(
                URI.create(providersProps.getPdlPerson().getUrl() + GRAPHQL_URL))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(PREPROD_ENV))
                        .header(HEADER_NAV_CONSUMER_TOKEN, stsOidcService.getIdToken(PREPROD_ENV))
                        .header(HEADER_NAV_CALL_ID, "Dolly: " + UUID.randomUUID().toString())
                        .header(TEMA, GEN.name())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(GraphQLRequest.builder()
                                .query(getQueryFromFile())
                                .variables(Map.of("ident", ident, "historikk", true))
                                .build()),
                JsonNode.class);
    }

    private static String getQueryFromFile() {

        val resource = new ClassPathResource("pdlperson/pdlquery.graphql");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(resource.getInputStream(), Consts.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));

        } catch (IOException e) {
            log.error("Lesing av query ressurs feilet");
            return null;
        }
    }
}
