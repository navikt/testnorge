package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    private long gruppeId = 10L;
    private String miljoe = "t9";
    private String endringskode = "0110";
    private int antallPerEndringskode = 2;
    private List<Long> expectedMeldingsIds;
    private GenereringsOrdreRequest ordreRequest;

    @Before
    public void setUp() {
        Map<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put(endringskode, antallPerEndringskode);
        ordreRequest = new GenereringsOrdreRequest(gruppeId, miljoe, antallMeldingerPerEndringskode);
        expectedMeldingsIds = new ArrayList<>();
        expectedMeldingsIds.add(120421016L);
        expectedMeldingsIds.add(110156008L);
    }

    /**
     * Scenario: Tester happypath til {@link TestnorgeSkdConsumer#startSyntetisering} - forventer at metoden returnerer id-ene til
     * de lagrede skdmeldingene i TPSF - forventer at metoden kaller Testnorge-Skd med de rette parametrene (se stub)
     */
    @Test
    public void shouldStartSyntetisering() {
        stubSkdConsumer();

        ResponseEntity response = testnorgeSkdConsumer.startSyntetisering(ordreRequest);

        SkdMeldingerTilTpsRespons skdMeldingerTilTpsRespons = (SkdMeldingerTilTpsRespons) response.getBody();

        assert skdMeldingerTilTpsRespons != null;
        assertTrue(skdMeldingerTilTpsRespons.getTpsfIds().containsAll(expectedMeldingsIds));
    }

    private void stubSkdConsumer() {
        stubFor(post(urlPathEqualTo("/skd/api/v1/syntetisering/generer"))
                .withRequestBody(equalToJson(
                        "{\"avspillergruppeId\":" + gruppeId
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
}
