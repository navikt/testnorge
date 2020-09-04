package no.nav.dolly.consumer.saf;

import static java.lang.String.format;
import static java.util.Objects.requireNonNull;
import static no.nav.dolly.domain.CommonKeys.CONSUMER;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CALL_ID;
import static no.nav.dolly.domain.CommonKeys.HEADER_NAV_CONSUMER_ID;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.http.Consts;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.google.common.io.CharStreams;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import no.nav.dolly.consumer.pdlperson.GraphQLRequest;
import no.nav.dolly.consumer.saf.domain.SafRequest;
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

    @Timed(name = "providers", tags = { "operation", "hent-inntektsmelding-dokumentinfo" })
    public List<JsonNode> getInntektsmeldingDokumentinfo(String environment, SafRequest request) {

        return getSamletDokumentinfo(
                sendJoarkMetadataQuery(environment, request.getJournalpostId(), "saf/safquery-inntektsmelding.graphql"),
                sendJoarkDokumentQuery(environment, request));
    }

    @Timed(name = "providers", tags = { "operation", "hent-dokarkiv-dokumentinfo" })
    public ResponseEntity<JsonNode> getDokarkivDokumentinfo(String environment, String journalpostId) {

        return sendJoarkMetadataQuery(environment, journalpostId, "saf/safquery-dokarkiv.graphql");
    }

    private List<JsonNode> getSamletDokumentinfo(ResponseEntity<JsonNode> node, ResponseEntity<String> xml) {
        List<JsonNode> samletJson = new ArrayList<>();
        try {
            if (node.hasBody()) {
                samletJson.add(node.getBody());
            }
            if (xml.hasBody()) {
                XmlMapper xmlMapper = new XmlMapper();
                samletJson.add(xmlMapper.readTree(xml.getBody()));
            }
        } catch (IOException e) {
            log.error("Json samling av dokument og metadata feilet: " + e);
        }
        return samletJson;
    }

    private ResponseEntity<String> sendJoarkDokumentQuery(String environment, SafRequest request) {

        return restTemplate.exchange(
                RequestEntity.get(URI.create(String.format("%s%s/%s/%s/%s", providersProps.getJoark().getUrl().replace("$", environment), SAF_URL,
                        request.getJournalpostId(), request.getDokumentInfoId(), request.getVariantFormat())))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(environment.contains(PREPROD_ENV) ? PREPROD_ENV : TEST_ENV))
                        .header(HEADER_NAV_CALL_ID, getNavCallId("SafDokumentRequest", environment))
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER).build(),
                String.class);
    }

    private ResponseEntity<JsonNode> sendJoarkMetadataQuery(String environment, String journalpostId, String graphqlFile) {

        String query = null;
        InputStream queryStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(graphqlFile);
        try {
            Reader reader = new InputStreamReader(requireNonNull(queryStream), Consts.UTF_8);
            query = CharStreams.toString(reader);
        } catch (IOException e) {
            log.error("Lesing av query ressurs feilet");
        }

        GraphQLRequest graphQLRequest = GraphQLRequest.builder()
                .query(query)
                .variables(Map.of("journalpostId", journalpostId))
                .build();

        return restTemplate.exchange(
                RequestEntity.post(URI.create(String.format("%s%s", providersProps.getJoark().getUrl().replace("$", environment),
                        SAF_GRAPHQL_URL)))
                        .header(AUTHORIZATION, stsOidcService.getIdToken(environment.contains(PREPROD_ENV) ? PREPROD_ENV : TEST_ENV))
                        .header(HEADER_NAV_CALL_ID, getNavCallId("SafMetadataRequest", environment))
                        .header(HEADER_NAV_CONSUMER_ID, CONSUMER)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(graphQLRequest),
                JsonNode.class);
    }

    private static String getNavCallId(String message, String environment) {

        String callId = format("%s %s", CONSUMER, UUID.randomUUID().toString());
        log.info("{} sendt, callId: {}, consumerId: {}, miljø: {}", message, callId, CONSUMER, environment);
        return callId;
    }
}
