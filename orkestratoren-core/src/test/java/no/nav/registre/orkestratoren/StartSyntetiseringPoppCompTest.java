package no.nav.registre.orkestratoren;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
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
    private String miljoe;
    private int antallNyeIdenter;

    @Before
    public void setUp() {
        this.gruppeId = 100000445L;
        this.miljoe = "t9";
        this.antallNyeIdenter = 1;
    }

    /**
     * Scenario: Happypath Sjekker at systemet sender og mottar riktige verdier i post-requesten til Testnorge-Sigrun.
     */
    @Test
    public void shouldStartSyntetisering() {
        stubTestnorgeSigrun();

        SyntetiserPoppRequest ordreRequest = new SyntetiserPoppRequest(gruppeId, miljoe, antallNyeIdenter);
        ResponseEntity response = syntetiseringsController.opprettSkattegrunnlagISigrun("test", ordreRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * Scenario: Tester at property-variablene blir henta riktig fra properties-fil når jobControlleren opprettes
     */
    @Test
    public void shouldGetProperties() {
        assertEquals(miljoe, jobController.getPoppbatchMiljoe());
        assertEquals(gruppeId, jobController.getAvspillergruppeId());
        assertEquals(antallNyeIdenter, jobController.getPoppbatchAntallNyeIdenter());
    }

    public void stubTestnorgeSigrun() {
        stubFor(post(urlPathEqualTo("/sigrun/api/v1/syntetisering/generer"))
                .withHeader("testdataEier", equalTo("test"))
                .withRequestBody(equalToJson("{\"avspillergruppeId\": " + gruppeId
                        + ", \"miljoe\": \"" + miljoe
                        + "\", \"antallNyeIdenter\": " + antallNyeIdenter + "}"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("[" + HttpStatus.OK + ", " + HttpStatus.OK + "]")));
    }
}
