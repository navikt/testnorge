package no.nav.registre.orkestratoren;

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
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class StartSyntetiseringTpCompTest {

    @Autowired
    private SyntetiseringsController syntetiseringsController;

    @Autowired
    private JobController jobController;

    private Long gruppeId;
    private String miljoe;
    private int antallIdenter;

    @Before
    public void setUp() {
        this.gruppeId = 100000445L;
        this.miljoe = "t9";
        this.antallIdenter = 1;
    }

    /**
     * Scenario: Når kontrolleren kalles skal det gjøres kall til tp adapter med korrekt request
     */
    @Test
    public void shouldStartSyntetisering() {
        stubTp();
        ResponseEntity entity = syntetiseringsController.opprettYtelserITp(new SyntetiserTpRequest(gruppeId, miljoe, antallIdenter));
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    /**
     * Scenario: Tester at property-variablene blir henta riktig fra properties-fil når jobControlleren opprettes
     */
    @Test
    public void shouldGetProperties() {
        assertThat(jobController.getAvspillergruppeIdMedMiljoe().keySet().toString(), containsString(gruppeId.toString()));
        assertThat(jobController.getAvspillergruppeIdMedMiljoe().values().toString(), containsString(miljoe));
        assertEquals(antallIdenter, jobController.getAaregbatchAntallNyeIdenter());
    }

    private void stubTp() {
        stubFor(post(urlPathEqualTo("/tp/api/v1/syntetisering/generer/")).withRequestBody(equalToJson(
                "{\"avspillergruppeId\": " + gruppeId +
                        ",\"miljoe\": \"" + miljoe + "\""+
                        ",\"antallPersoner\": " + antallIdenter +
                        "}"
        )).willReturn(ok()));
    }
}
