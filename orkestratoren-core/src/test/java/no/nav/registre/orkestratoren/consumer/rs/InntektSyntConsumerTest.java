package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.hamcrest.CoreMatchers;
import org.hamcrest.Matchers;
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

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@RunWith(SpringRunner.class)
@RestClientTest(InntektSyntConsumer.class)
@ActiveProfiles("test")
public class InntektSyntConsumerTest {

    @Autowired
    private InntektSyntConsumer inntektSyntConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-inntekt.rest.api.url}")
    private String serverUrl;

    private long gruppeId = 10L;
    private String fnr = "01010101010";
    private double beloep = 10000;
    private SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest;

    @Before
    public void setUp() {
        syntetiserInntektsmeldingRequest = new SyntetiserInntektsmeldingRequest(gruppeId);
    }

    /**
     * Scenario: Tester happypath til {@link InntektSyntConsumer#startSyntetisering} - forventer at metoden returnerer
     * feilede inntektsmeldinger. Forventer at metoden kaller Testnorge-Inntekt med de rette parametrene (se stub)
     */
    @Test
    public void shouldStartSyntetisering() {
        String expectedUri = serverUrl + "/v1/syntetisering/generer";
        stubInntektConsumer(expectedUri);

        Map<String, List<Object>> feiledeInntektsmeldinger = inntektSyntConsumer.startSyntetisering(syntetiserInntektsmeldingRequest);

        assertThat(feiledeInntektsmeldinger.keySet(), Matchers.contains(fnr));
        Map<String, String> inntekt = (Map<String, String>) feiledeInntektsmeldinger.get(fnr).get(0);
        assertThat(inntekt.get("beloep"), Matchers.is(beloep));
    }

    private void stubInntektConsumer(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\":" + gruppeId + "}"))
                .andRespond(withSuccess("{\n"
                        + "  \"" + fnr + "\": [\n"
                        + "    {\n"
                        + "      \"beloep\": " + beloep + ",\n"
                        + "      \"inntektstype\": \"\",\n"
                        + "      \"aar\": \"2019\",\n"
                        + "      \"maaned\": \"09\",\n"
                        + "      \"inntektsinformasjonsType\": \"\",\n"
                        + "      \"inngaarIGrunnlagForTrekk\": \"\",\n"
                        + "      \"utloeserArbeidsgiveravgift\": \"\",\n"
                        + "      \"virksomhet\": \"\"\n"
                        + "    }\n"
                        + "  ]\n"
                        + "}", MediaType.APPLICATION_JSON));
    }
}