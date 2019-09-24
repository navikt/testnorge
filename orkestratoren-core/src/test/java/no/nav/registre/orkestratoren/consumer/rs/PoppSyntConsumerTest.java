package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.consumer.rs.response.SletteSkattegrunnlagResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class PoppSyntConsumerTest {

    @Autowired
    private PoppSyntConsumer poppSyntConsumer;

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
     * Scenario: Tester happypath til {@link PoppSyntConsumer#startSyntetisering} - forventer at metoden returnerer statuskodene
     * gitt av sigrun-skd-stub. Forventer at metoden kaller Testnorge-Sigrun med de rette parametrene (se stub)
     */
    @Test
    public void shouldStartSyntetisering() {
        stubPoppConsumerStartSyntetisering();

        ResponseEntity response = poppSyntConsumer.startSyntetisering(syntetiserPoppRequest, "test");

        assertThat((List<Integer>)response.getBody(), containsInAnyOrder(HttpStatus.OK.value(), HttpStatus.INTERNAL_SERVER_ERROR.value()));
    }

    @Test
    public void shouldDeleteIdenterFromSigrun() {
        stubPoppConsumerSlettIdenter();

        SletteSkattegrunnlagResponse response = poppSyntConsumer.slettIdenterFraSigrun(testdataEier, MILJOE, identer);

        assertThat(response.getGrunnlagSomIkkeKunneSlettes().get(0).getPersonidentifikator(), equalTo(identer.get(0)));
        assertThat(response.getGrunnlagSomBleSlettet().get(0).getPersonidentifikator(), equalTo(identer.get(1)));
    }

    private void stubPoppConsumerStartSyntetisering() {
        stubFor(post(urlPathEqualTo("/sigrun/api/v1/syntetisering/generer"))
                .withHeader("testdataEier", matching("test"))
                .withRequestBody(equalToJson(
                        "{\"avspillergruppeId\":" + AVSPILLERGRUPPE_ID
                                + ",\"miljoe\":\"" + MILJOE + "\""
                                + ",\"antallNyeIdenter\":" + identer.size() + "}"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + HttpStatus.OK.value() + "\", \"" + HttpStatus.INTERNAL_SERVER_ERROR.value() + "\"]")));
    }

    private void stubPoppConsumerSlettIdenter() {
        stubFor(delete(urlPathEqualTo("/sigrun/api/v1/ident"))
                .withHeader("testdataEier", matching(testdataEier))
                .withRequestBody(equalToJson(
                        "[\"" + identer.get(0) + "\", \"" + identer.get(1) + "\"]"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{"
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
                                + "}")));
    }
}
