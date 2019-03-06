package no.nav.registre.orkestratoren;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

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

import no.nav.registre.orkestratoren.batch.JobController;
import no.nav.registre.orkestratoren.provider.rs.SyntetiseringsController;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class StartSyntetiseringPoppCompTest {

    @Autowired
    private SyntetiseringsController syntetiseringsController;

    @Autowired
    private JobController jobController;

    private Long gruppeId;
    private String miljoe1;
    private String miljoe2;
    private int antallNyeIdenter;

    @Before
    public void setUp() {
        this.gruppeId = 100000445L;
        this.miljoe1 = "t9";
        this.miljoe2 = "t10";
        this.antallNyeIdenter = 1;
    }

    /**
     * Scenario: Happypath Sjekker at systemet sender og mottar riktige verdier i post-requesten til Testnorge-Sigrun.
     */
    @Test
    public void shouldStartSyntetisering() {
        stubTestnorgeSigrun();

        SyntetiserPoppRequest ordreRequest = new SyntetiserPoppRequest(gruppeId, miljoe1, antallNyeIdenter);
        ResponseEntity response = syntetiseringsController.opprettSkattegrunnlagISigrun("test", ordreRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Scenario: Tester at property-variablene blir henta riktig fra properties-fil når jobControlleren opprettes
     */
    @Test
    public void shouldGetProperties() {
        assertThat(jobController.getAvspillergruppeIdMedMiljoe().keySet().toString(), containsString(gruppeId.toString()));
        assertThat(jobController.getAvspillergruppeIdMedMiljoe().values().toString(), containsString(miljoe1));
        assertThat(jobController.getAvspillergruppeIdMedMiljoe().values().toString(), containsString(miljoe2));
        assertEquals(antallNyeIdenter, jobController.getPoppbatchAntallNyeIdenter());
    }

    public void stubTestnorgeSigrun() {
        stubFor(post(urlPathEqualTo("/sigrun/api/v1/syntetisering/generer"))
                .withHeader("testdataEier", equalTo("test"))
                .withRequestBody(equalToJson("{\"avspillergruppeId\": " + gruppeId
                        + ", \"miljoe\": \"" + miljoe1
                        + "\", \"antallNyeIdenter\": " + antallNyeIdenter + "}"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("[" + HttpStatus.OK + ", " + HttpStatus.OK + "]")));
    }
}
