package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.Assert.assertEquals;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class HodejegerenConsumerTest {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    private long gruppeId = 10L;
    private String miljoe = "t9";
    private String endringskode = "0110";
    private int antallPerEndringskode = 2;
    private List<Long> expectedMeldingsIds;
    private Map<String, Integer> antallMeldingerPerEndringskode;
    private GenereringsOrdreRequest ordreRequest;

    @Before
    public void setUp() {
        antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put(endringskode, antallPerEndringskode);
        ordreRequest = new GenereringsOrdreRequest(gruppeId, miljoe, antallMeldingerPerEndringskode);
        expectedMeldingsIds = new ArrayList<>();
        expectedMeldingsIds.add(120421016L);
        expectedMeldingsIds.add(110156008L);
    }

    /**
     * Scenario: Tester happypath til {@link HodejegerenConsumer#startSyntetisering} - forventer at metoden returnerer id-ene til de
     * lagrede skdmeldingene i TPSF - forventer at metoden kaller hodejegeren med de rette parametrene (se stub)
     */
    @Test
    public void shouldStartSyntetisering() {
        stubHodejegerenConsumer();

        List<Long> ids = hodejegerenConsumer.startSyntetisering(ordreRequest);

        assertEquals(expectedMeldingsIds.toString(), ids.toString());
    }

    public void stubHodejegerenConsumer() {
        stubFor(post(urlPathEqualTo("/hodejegeren/api/v1/syntetisering/generer"))
                .withRequestBody(equalToJson(
                        "{\"avspillergruppeId\":" + gruppeId
                                + ",\"miljoe\":\"" + miljoe
                                + "\",\"antallMeldingerPerEndringskode\":{\"" + endringskode + "\":" + antallPerEndringskode + "}}"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[" + expectedMeldingsIds.get(0) + ", " + expectedMeldingsIds.get(1) + "]")));
    }
}
