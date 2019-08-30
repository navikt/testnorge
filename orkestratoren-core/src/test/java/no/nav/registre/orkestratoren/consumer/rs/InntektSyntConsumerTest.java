package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class InntektSyntConsumerTest {

    @Autowired
    private InntektSyntConsumer inntektSyntConsumer;

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
        stubInntektConsumer();

        String id = inntektSyntConsumer.startSyntetisering(syntetiserInntektsmeldingRequest);

        assertEquals(expectedId, id);
    }

    private void stubInntektConsumer() {
        stubFor(post(urlPathEqualTo("/inntekt/api/v1/syntetisering/generer"))
                .withRequestBody(equalToJson(
                        "{\"avspillergruppeId\":" + gruppeId + "}"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(expectedId)));
    }
}