package no.nav.dolly.bestilling.saf;

import static java.lang.String.format;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
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
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.common.io.CharStreams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.bestilling.saf.domain.SafRequest;
import no.nav.dolly.consumer.pdlperson.GraphQLRequest;
import no.nav.dolly.metrics.Timed;
import no.nav.dolly.properties.ProvidersProps;
import no.nav.dolly.security.sts.StsOidcService;

@Slf4j
@Service
@RequiredArgsConstructor
public class SafConsumer {

    private static final String SAF_URL = "/rest/hentdokument";
    private static final String SAF_GRAPHQL_URL = "/graphql";
    private static final String PREPROD_ENV = "q";
    private static final String TEST_ENV = "t";

    private final RestTemplate restTemplate;
    private final ProvidersProps providersProps;
    private final StsOidcService stsOidcService;

    @Timed(name = "providers", tags = { "operation", "hent-dokument" })
    public ResponseEntity<String> getDokument(String environment, SafRequest request) {

        String callId = getNavCallId();
        log.info("Dokarkiv melding sendt, callId: {}, consumerId: {}, miljø: {}", callId, CONSUMER, environment);

        return restTemplate.exchange(
                RequestEntity.get(URI.create(String.format("%s%s", providersProps.getSaf().getUrl().replace("$", environment),
                        String.format("%s/%s/%s/%s", SAF_URL, request.getJournalpostId(), request.getDokumentInfoId(), request.getVariantFormat()))))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(environment.contains(PREPROD_ENV) ? PREPROD_ENV : TEST_ENV))
                        .header(HEADER_NAV_CALL_ID, callId)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER).build(),
                String.class);
    }

    @Timed(name = "providers", tags = { "operation", "hent-metadata" })
    public ResponseEntity<JsonNode> getMetadata(String environment, Long journalpostId) {

        Map<String, Object> variables = new HashMap<>();
        variables.put("journalpostId", journalpostId);

        String query = null;
        InputStream queryStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("joark/safquery.graphql");
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

        String callId = getNavCallId();
        log.info("SafMetadataRequest sendt, callId: {}, consumerId: {}, miljø: {}", callId, CONSUMER, environment);

        return restTemplate.exchange(
                RequestEntity.post(URI.create(String.format("%s%s", providersProps.getSaf().getUrl().replace("$", "q1"),
                        SAF_GRAPHQL_URL)))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(environment.contains(PREPROD_ENV) ? PREPROD_ENV : TEST_ENV))
                        .header(HEADER_NAV_CALL_ID, callId)
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(graphQLRequest),
                JsonNode.class);
    }

    private static String getNavCallId() {
        return format("%s %s", CONSUMER, UUID.randomUUID().toString());
    }
}
