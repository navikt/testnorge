package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class EiaSyntConsumerTest {

    @Autowired
    private EiaSyntConsumer eiaSyntConsumer;

    private long gruppeId = 10L;
    private String miljoe = "t9";
    private int antallMeldinger = 2;
    private SyntetiserEiaRequest syntetiserEiaRequest;
    private List<String> expectedIdenter;

    @Before
    public void setUp() {
        syntetiserEiaRequest = new SyntetiserEiaRequest(gruppeId, miljoe, antallMeldinger);
        expectedIdenter = new ArrayList<>();
        expectedIdenter.add("01010101010");
        expectedIdenter.add("02020202020");
    }

    /**
     * Scenario: Tester happypath til {@link EiaSyntConsumer#startSyntetisering} - forventer at metoden returnerer identene til de
     * opprettede sykemeldingene - forventer at metoden kaller eias-emottakstub med de rette parametrene (se stub)
     */
    @Test
    public void shouldStartSyntetisering() {
        stubEiaSyntConsumer();

        List<String> identer = eiaSyntConsumer.startSyntetisering(syntetiserEiaRequest);

        assertEquals(expectedIdenter.toString(), identer.toString());
    }

    public void stubEiaSyntConsumer() {
        stubFor(post(urlPathEqualTo("/eia/api/v1/syntetisering/generer/QA.Q414.FS06_EIA_MELDINGER"))
                .withRequestBody(equalToJson(
                        "{\"avspillergruppeId\":" + gruppeId
                                + ",\"miljoe\":\"" + miljoe + "\""
                                + ",\"antallMeldinger\":" + antallMeldinger + "}"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + expectedIdenter.get(0) + "\", \"" + expectedIdenter.get(1) + "\"]")));
    }
}
