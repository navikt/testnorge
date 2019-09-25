package no.nav.registre.orkestratoren.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;

@RunWith(SpringRunner.class)
@RestClientTest(TestnorgeSkdConsumer.class)
@ActiveProfiles("test")
public class TestnorgeSkdConsumerTest {

    @Autowired
    private TestnorgeSkdConsumer testnorgeSkdConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-skd.rest-api.url}")
    private String serverUrl;

    private long avspillergruppeId = 10L;
    private String miljoe = "t9";
    private String endringskode = "0110";
    private int antallPerEndringskode = 2;
    private List<Long> expectedMeldingsIds;
    private SyntetiserSkdmeldingerRequest ordreRequest;
    private List<String> identer;

    @Before
    public void setUp() {
        Map<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put(endringskode, antallPerEndringskode);
        ordreRequest = new SyntetiserSkdmeldingerRequest(avspillergruppeId, miljoe, antallMeldingerPerEndringskode);
        expectedMeldingsIds = new ArrayList<>();
        expectedMeldingsIds.add(120421016L);
        expectedMeldingsIds.add(110156008L);
        identer = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));
    }

    /**
     * Scenario: Tester happypath til {@link TestnorgeSkdConsumer#startSyntetisering} - forventer at metoden returnerer id-ene til
     * de lagrede skdmeldingene i TPSF - forventer at metoden kaller Testnorge-Skd med de rette parametrene (se stub)
     */
    @Test
    public void shouldStartSyntetisering() {
        String expectedUri = serverUrl + "/v1/syntetisering/generer";
        stubSkdConsumerStartSyntetisering(expectedUri);

        ResponseEntity response = testnorgeSkdConsumer.startSyntetisering(ordreRequest);

        SkdMeldingerTilTpsRespons skdMeldingerTilTpsRespons = (SkdMeldingerTilTpsRespons) response.getBody();

        assert skdMeldingerTilTpsRespons != null;
        assertThat(skdMeldingerTilTpsRespons.getAntallSendte(), equalTo(2));
    }

    @Test
    public void shouldDeleteIdenterFromAvspillergruppe() {
        String expectedUri = serverUrl + "/v1/ident/{avspillergruppeId}";
        stubSkdConsumerSlettIdenter(expectedUri);

        List<Long> response = testnorgeSkdConsumer.slettIdenterFraAvspillerguppe(avspillergruppeId, identer);

        assertThat(response, IsIterableContainingInOrder.contains(expectedMeldingsIds.get(0), expectedMeldingsIds.get(1)));
    }

    private void stubSkdConsumerStartSyntetisering(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\":" + avspillergruppeId
                        + ",\"miljoe\":\"" + miljoe
                        + "\",\"antallMeldingerPerEndringskode\":{\"" + endringskode + "\":" + antallPerEndringskode + "}}"))
                .andRespond(withSuccess("{\"antallSendte\": \"" + expectedMeldingsIds.size()
                        + "\", \"antallFeilet\": \"" + 0
                        + "\", \"statusFraFeilendeMeldinger\": ["
                        + "{\"foedselsnummer\": \"" + "01010101010"
                        + "\", \"sekvensnummer\": \"\""
                        + ", \"status\": \"\"},"
                        + "{\"foedselsnummer\": \"" + "02020202020"
                        + "\", \"sekvensnummer\": \"\""
                        + ", \"status\": \"\"}]}", MediaType.APPLICATION_JSON));
    }

    private void stubSkdConsumerSlettIdenter(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri, avspillergruppeId))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(content().json("[\"" + identer.get(0) + "\", \"" + identer.get(1) + "\"]"))
                .andRespond(withSuccess("[" + expectedMeldingsIds.get(0) + ", " + expectedMeldingsIds.get(1) + "]", MediaType.APPLICATION_JSON));
    }
}
