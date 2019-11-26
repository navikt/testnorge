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
import java.util.Arrays;
import java.util.List;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserElsamRequest;

@RunWith(SpringRunner.class)
@RestClientTest(TestnorgeElsamConsumer.class)
@ActiveProfiles("test")
public class TestnorgeElsamConsumerTest {

    @Autowired
    private TestnorgeElsamConsumer testnorgeElsamConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-elsam.rest.api.url}")
    private String serverUrl;

    private long gruppeId = 10L;
    private String miljoe = "t9";
    private int antallIdenter = 2;
    private SyntetiserElsamRequest syntetiserElsamRequest;
    private List<String> expectedIdenter;

    @Before
    public void setUp() {
        syntetiserElsamRequest = new SyntetiserElsamRequest(gruppeId, miljoe, antallIdenter);
        expectedIdenter = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));
    }

    /**
     * Scenario: Tester happypath til {@link TestnorgeElsamConsumer#startSyntetisering} - forventer at metoden returnerer identene til de
     * opprettede sykemeldingene - forventer at metoden kaller testnorge-elsam med de rette parametrene (se adapter)
     */
    @Test
    public void shouldStartSyntetisering() {
        var expectedUri = serverUrl + "/v1/syntetisering/generer";
        stubElsamSyntConsumer(expectedUri);

        var identer = testnorgeElsamConsumer.startSyntetisering(syntetiserElsamRequest);

        assertEquals(expectedIdenter.toString(), identer.toString());
    }

    private void stubElsamSyntConsumer(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\":" + gruppeId
                        + ",\"miljoe\":\"" + miljoe + "\""
                        + ",\"antallIdenter\":" + antallIdenter + "}"))
                .andRespond(withSuccess("[\"" + expectedIdenter.get(0) + "\", \"" + expectedIdenter.get(1) + "\"]", MediaType.APPLICATION_JSON));
    }
}
