package no.nav.registre.orkestratoren.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

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

import java.util.Map;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@RunWith(SpringRunner.class)
@RestClientTest(TestnorgeInntektConsumer.class)
@ActiveProfiles("test")
public class TestnorgeInntektConsumerTest {

    @Autowired
    private TestnorgeInntektConsumer testnorgeInntektConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-inntekt.rest.api.url}")
    private String serverUrl;

    private long gruppeId = 10L;
    private String fnr = "01010101010";
    private Double beloep = 10000.0;
    private SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest;

    @Before
    public void setUp() {
        syntetiserInntektsmeldingRequest = new SyntetiserInntektsmeldingRequest(gruppeId);
    }

    /**
     * Scenario: Tester happypath til {@link TestnorgeInntektConsumer#startSyntetisering} - forventer at metoden returnerer
     * feilede inntektsmeldinger. Forventer at metoden kaller Testnorge-Inntekt med de rette parametrene (se stub)
     */
    @Test
    public void shouldStartSyntetisering() {
        var expectedUri = serverUrl + "/v1/syntetisering/generer";
        stubInntektConsumer(expectedUri);

        var feiledeInntektsmeldinger = testnorgeInntektConsumer.startSyntetisering(syntetiserInntektsmeldingRequest);

        assertThat(feiledeInntektsmeldinger.keySet(), Matchers.contains(fnr));
        Map<String, Object> inntekt = (Map<String, Object>) feiledeInntektsmeldinger.get(fnr).get(0);
        assertThat(inntekt.get("beloep").toString(), equalTo(beloep.toString()));
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