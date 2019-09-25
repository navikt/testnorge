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

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@RunWith(SpringRunner.class)
@RestClientTest(InntektSyntConsumer.class)
@ActiveProfiles("test")
public class InntektSyntConsumerTest {

    @Autowired
    private InntektSyntConsumer inntektSyntConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${inntekt.rest.api.url}")
    private String serverUrl;

    private long gruppeId = 10L;
    private String expectedId = "1234";
    private SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest;

    @Before
    public void setUp() {
        syntetiserInntektsmeldingRequest = new SyntetiserInntektsmeldingRequest(gruppeId);
    }

    /**
     * Scenario: Tester happypath til {@link InntektSyntConsumer#startSyntetisering} - forventer at metoden returnerer id-en
     * gitt av inntekt. Forventer at metoden kaller Testnorge-Inntekt med de rette parametrene (se stub)
     */
    @Test
    public void shouldStartSyntetisering() {
        String expectedUri = serverUrl + "/v1/syntetisering/generer";
        stubInntektConsumer(expectedUri);

        String id = inntektSyntConsumer.startSyntetisering(syntetiserInntektsmeldingRequest);

        assertEquals(expectedId, id);
    }

    private void stubInntektConsumer(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\":" + gruppeId + "}"))
                .andRespond(withSuccess(expectedId, MediaType.APPLICATION_JSON));
    }
}