package no.nav.registre.orkestratoren;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;
import no.nav.registre.orkestratoren.provider.rs.SyntetiseringsController;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSkdmeldingerRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class StartSyntetiseringSkdCompTest {

    @Autowired
    private SyntetiseringsController syntetiseringsController;

    @Autowired
    private JobController jobController;

    private Long gruppeId;
    private String miljoe1;
    private String miljoe2;
    private String endringskode1, endringskode2;
    private int antallEndringskode1, antallEndringskode2;
    private Map<String, Integer> antallMeldingerPerEndringskode;
    private List<Long> expectedMeldingIds;

    private int expectedAntallSendte;
    private int expectedAntallFeilet;
    private String expectedFoedselnummer;
    private Long expectedSekvensnummer;
    private String expectedStatus;

    @Before
    public void setUp() {
        this.gruppeId = 100000445L;
        this.miljoe1 = "t9";
        this.miljoe2 = "t10";
        this.endringskode1 = "0110";
        this.endringskode2 = "0211";
        this.antallEndringskode1 = 10;
        this.antallEndringskode2 = 20;
        this.antallMeldingerPerEndringskode = new HashMap<>();
        this.antallMeldingerPerEndringskode.put(endringskode1, antallEndringskode1);
        this.expectedMeldingIds = new ArrayList<>(Arrays.asList(120421016L, 110156008L));

        this.expectedAntallSendte = 1;
        this.expectedAntallFeilet = 0;
        this.expectedFoedselnummer = "01010101010";
        this.expectedSekvensnummer = 10L;
        this.expectedStatus = "ok";
    }

    /**
     * Scenario: Happypath Sjekker at systemet sender og mottar riktige verdier i post-requesten til Testnorge-Skd.
     */
    @Test
    public void shouldStartSyntetisering() {
        stubSkd();

        SyntetiserSkdmeldingerRequest ordreRequest = new SyntetiserSkdmeldingerRequest(gruppeId, miljoe1, antallMeldingerPerEndringskode);

        ResponseEntity response = syntetiseringsController.opprettSkdmeldingerITPS(ordreRequest);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        SkdMeldingerTilTpsRespons skdMeldingerTilTpsRespons = ((SkdMeldingerTilTpsRespons) response.getBody());
        assert skdMeldingerTilTpsRespons != null;
        assertEquals(expectedAntallSendte, skdMeldingerTilTpsRespons.getAntallSendte());
        assertEquals(expectedAntallFeilet, skdMeldingerTilTpsRespons.getAntallFeilet());
        assertEquals(expectedFoedselnummer, skdMeldingerTilTpsRespons.getStatusFraFeilendeMeldinger().get(0).getFoedselsnummer());
        assertEquals(expectedSekvensnummer, skdMeldingerTilTpsRespons.getStatusFraFeilendeMeldinger().get(0).getSekvensnummer());
        assertEquals(expectedStatus, skdMeldingerTilTpsRespons.getStatusFraFeilendeMeldinger().get(0).getStatus());

    }

    /**
     * Scenario: Tester at property-variablene blir henta riktig fra properties-fil når jobControlleren opprettes
     */
    @Test
    public void shouldGetProperties() {
        assertThat(jobController.getAvspillergruppeIdMedMiljoe().keySet().toString(), containsString(gruppeId.toString()));
        assertThat(jobController.getAvspillergruppeIdMedMiljoe().values().toString(), containsString(miljoe1));
        assertThat(jobController.getAvspillergruppeIdMedMiljoe().values().toString(), containsString(miljoe2));

        Map<String, Integer> testMap = new HashMap<>(jobController.getAntallSkdmeldingerPerEndringskode());

        assertTrue(testMap.containsKey(endringskode1));
        assertEquals(antallEndringskode1, testMap.get(endringskode1).intValue());
        assertTrue(testMap.containsKey(endringskode2));
        assertEquals(antallEndringskode2, testMap.get(endringskode2).intValue());
    }

    public void stubSkd() {
        stubFor(post(urlPathEqualTo("/skd/api/v1/syntetisering/generer"))
                .withRequestBody(equalToJson("{\"avspillergruppeId\": " + gruppeId
                        + ", \"miljoe\": \"" + miljoe1
                        + "\", \"antallMeldingerPerEndringskode\": {\"" + endringskode1 + "\": " + antallMeldingerPerEndringskode.get(endringskode1) + "}}"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("{\"antallSendte\": \"" + expectedAntallSendte
                                + "\", \"antallFeilet\": \"" + expectedAntallFeilet
                                + "\", \"statusFraFeilendeMeldinger\": ["
                                + "{\"foedselsnummer\": \"" + expectedFoedselnummer
                                + "\", \"sekvensnummer\": \"" + expectedSekvensnummer + "\""
                                + ", \"status\": \"" + expectedStatus + "\"}],"
                                + "\"tpsfIds\": [\"" + expectedMeldingIds.get(0) + "\", \"" + expectedMeldingIds.get(1) + "\"]}")));
    }
}
