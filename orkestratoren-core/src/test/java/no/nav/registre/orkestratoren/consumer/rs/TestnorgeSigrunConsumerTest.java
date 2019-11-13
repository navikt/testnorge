package no.nav.registre.orkestratoren.consumer.rs;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.response.SletteSkattegrunnlagResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;

@RunWith(SpringRunner.class)
@RestClientTest(TestnorgeSigrunConsumer.class)
@ActiveProfiles("test")
public class TestnorgeSigrunConsumerTest {

    @Autowired
    private TestnorgeSigrunConsumer testnorgeSigrunConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-sigrun.rest.api.url}")
    private String serverUrl;

    private static final Long AVSPILLERGRUPPE_ID = 123L;
    private static final String MILJOE = "t1";
    private List<String> identer;
    private SyntetiserPoppRequest syntetiserPoppRequest;
    private String testdataEier = "test";

    @Before
    public void setUp() {
        identer = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));
        syntetiserPoppRequest = new SyntetiserPoppRequest(AVSPILLERGRUPPE_ID, MILJOE, identer.size());
    }

    /**
     * Scenario: Tester happypath til {@link TestnorgeSigrunConsumer#startSyntetisering} - forventer at metoden returnerer statuskodene
     * gitt av sigrun-skd-stub. Forventer at metoden kaller Testnorge-Sigrun med de rette parametrene (se stub)
     */
    @Test
    public void shouldStartSyntetisering() {
        String expectedUri = serverUrl + "/v1/syntetisering/generer";
        stubPoppConsumerStartSyntetisering(expectedUri);

        ResponseEntity response = testnorgeSigrunConsumer.startSyntetisering(syntetiserPoppRequest, testdataEier);

        assertThat(response.getBody().toString(), containsString(String.valueOf(HttpStatus.OK.value())));
    }

    @Test
    public void shouldDeleteIdenterFromSigrun() {
        String expectedUri = serverUrl + "/v1/ident?miljoe={miljoe}";
        stubPoppConsumerSlettIdenter(expectedUri);

        SletteSkattegrunnlagResponse response = testnorgeSigrunConsumer.slettIdenterFraSigrun(testdataEier, MILJOE, identer);

        assertThat(response.getGrunnlagSomIkkeKunneSlettes().get(0).getPersonidentifikator(), equalTo(identer.get(0)));
        assertThat(response.getGrunnlagSomBleSlettet().get(0).getPersonidentifikator(), equalTo(identer.get(1)));
    }

    private void stubPoppConsumerStartSyntetisering(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("testdataEier", testdataEier))
                .andExpect(content().json("{\"avspillergruppeId\":" + AVSPILLERGRUPPE_ID
                        + ",\"miljoe\":\"" + MILJOE + "\""
                        + ",\"antallNyeIdenter\":" + identer.size() + "}"))
                .andRespond(withSuccess("[\"" + HttpStatus.OK.value() + "\", \"" + HttpStatus.INTERNAL_SERVER_ERROR.value() + "\"]", MediaType.APPLICATION_JSON));
    }

    private void stubPoppConsumerSlettIdenter(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri, MILJOE))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header("testdataEier", testdataEier))
                .andExpect(content().json("[\"" + identer.get(0) + "\", \"" + identer.get(1) + "\"]"))
                .andRespond(withSuccess("{"
                        + "  \"grunnlagSomIkkeKunneSlettes\": "
                        + "    [{\"personidentifikator\": \"" + identer.get(0) + "\","
                        + "    \"inntektsaar\": \"1968\","
                        + "    \"tjeneste\": \"Beregnet skatt\","
                        + "    \"grunnlag\": \"personinntektFiskeFangstFamiliebarnehage\","
                        + "    \"verdi\": \"874\","
                        + "    \"testdataEier\": \"test\"}],"
                        + "  \"grunnlagSomBleSlettet\": "
                        + "    [{\"personidentifikator\": \"" + identer.get(1) + "\","
                        + "    \"inntektsaar\": \"1969\","
                        + "    \"tjeneste\": \"Beregnet skatt\","
                        + "    \"grunnlag\": \"personinntektFiskeFangstFamiliebarnehage\","
                        + "    \"verdi\": \"823\","
                        + "    \"testdataEier\": \"test\"}],"
                        + "  \"identerMedGrunnlagFraAnnenTestdataEier\": []"
                        + "}", MediaType.APPLICATION_JSON));
    }
}
