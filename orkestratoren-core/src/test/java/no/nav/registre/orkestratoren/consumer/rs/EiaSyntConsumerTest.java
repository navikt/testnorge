package no.nav.registre.orkestratoren.consumer.rs;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;

@RunWith(SpringRunner.class)
@RestClientTest(EiaSyntConsumer.class)
@ActiveProfiles("test")
public class EiaSyntConsumerTest {

    @Autowired
    private EiaSyntConsumer eiaSyntConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-eia.rest.api.url}")
    private String serverUrl;

    private long gruppeId = 10L;
    private String miljoe = "t9";
    private int antallIdenter = 2;
    private SyntetiserEiaRequest syntetiserEiaRequest;
    private List<String> expectedIdenter;

    @Before
    public void setUp() {
        syntetiserEiaRequest = new SyntetiserEiaRequest(gruppeId, miljoe, antallIdenter);
        expectedIdenter = new ArrayList<>();
        expectedIdenter.add("01010101010");
        expectedIdenter.add("02020202020");
    }

    /**
     * Scenario: Tester happypath til {@link EiaSyntConsumer#startSyntetisering} - forventer at metoden returnerer identene til de
     * opprettede sykemeldingene - forventer at metoden kaller testnorge-eia med de rette parametrene (se stub)
     */
    @Test
    public void shouldStartSyntetisering() {
        String expectedUri = serverUrl + "/v1/syntetisering/generer/QA.Q414.FS06_EIA_MELDINGER";
        stubEiaSyntConsumer(expectedUri);

        List<String> identer = eiaSyntConsumer.startSyntetisering(syntetiserEiaRequest);

        assertEquals(expectedIdenter.toString(), identer.toString());
    }

    private void stubEiaSyntConsumer(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\":" + gruppeId
                        + ",\"miljoe\":\"" + miljoe + "\""
                        + ",\"antallIdenter\":" + antallIdenter + "}"))
                .andRespond(withSuccess("[\"" + expectedIdenter.get(0) + "\", \"" + expectedIdenter.get(1) + "\"]", MediaType.APPLICATION_JSON));
    }
}
