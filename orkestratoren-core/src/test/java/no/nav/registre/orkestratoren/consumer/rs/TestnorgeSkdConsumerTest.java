package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class TestnorgeSkdConsumerTest {

    @Autowired
    private TestnorgeSkdConsumer testnorgeSkdConsumer;

    private long avspillergruppeId = 10L;
    private String miljoe = "t9";
    private String endringskode = "0110";
    private int antallPerEndringskode = 2;
    private List<Long> expectedMeldingsIds;
    private GenereringsOrdreRequest ordreRequest;
    private List<String> identer;

    @Before
    public void setUp() {
        Map<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put(endringskode, antallPerEndringskode);
        ordreRequest = new GenereringsOrdreRequest(avspillergruppeId, miljoe, antallMeldingerPerEndringskode);
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
        stubSkdConsumerStartSyntetisering();

        ResponseEntity response = testnorgeSkdConsumer.startSyntetisering(ordreRequest);

        SkdMeldingerTilTpsRespons skdMeldingerTilTpsRespons = (SkdMeldingerTilTpsRespons) response.getBody();

        assert skdMeldingerTilTpsRespons != null;
        assertThat(skdMeldingerTilTpsRespons.getTpsfIds(), IsIterableContainingInOrder.contains(expectedMeldingsIds.get(0), expectedMeldingsIds.get(1)));
    }

    @Test
    public void shouldDeleteIdenterFromAvspillergruppe() {
        stubSkdConsumerSlettIdenter();

        List<Long> response = testnorgeSkdConsumer.slettIdenterFraAvspillerguppe(avspillergruppeId, identer);

        assertThat(response, IsIterableContainingInOrder.contains(expectedMeldingsIds.get(0), expectedMeldingsIds.get(1)));

    }

    private void stubSkdConsumerStartSyntetisering() {
        stubFor(post(urlPathEqualTo("/skd/api/v1/syntetisering/generer"))
                .withRequestBody(equalToJson(
                        "{\"avspillergruppeId\":" + avspillergruppeId
                                + ",\"miljoe\":\"" + miljoe
                                + "\",\"antallMeldingerPerEndringskode\":{\"" + endringskode + "\":" + antallPerEndringskode + "}}"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("{\"antallSendte\": \"" + expectedMeldingsIds.size()
                                + "\", \"antallFeilet\": \"" + 0
                                + "\", \"statusFraFeilendeMeldinger\": ["
                                + "{\"foedselsnummer\": \"" + "01010101010"
                                + "\", \"sekvensnummer\": \"\""
                                + ", \"status\": \"\"},"
                                + "{\"foedselsnummer\": \"" + "02020202020"
                                + "\", \"sekvensnummer\": \"\""
                                + ", \"status\": \"\"}],"
                                + "\"tpsfIds\": [\"" + expectedMeldingsIds.get(0) + "\", \"" + expectedMeldingsIds.get(1) + "\"]}")));
    }

    private void stubSkdConsumerSlettIdenter() {
        stubFor(delete(urlPathEqualTo("/skd/api/v1/ident/" + avspillergruppeId))
                .withRequestBody(equalToJson("[\"" + identer.get(0) + "\", \"" + identer.get(1) + "\"]"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("[" + expectedMeldingsIds.get(0) + ", " + expectedMeldingsIds.get(1) + "]")));
    }
}
