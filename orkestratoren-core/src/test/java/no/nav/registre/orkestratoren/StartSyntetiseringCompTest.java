package no.nav.registre.orkestratoren;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.consumer.rs.response.AvspillingResponse;
import no.nav.registre.orkestratoren.provider.rs.SyntetiserSkdmeldingerRequest;
import no.nav.registre.orkestratoren.provider.rs.SyntetiseringsController;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class StartSyntetiseringCompTest {

    @Autowired
    private SyntetiseringsController syntetiseringsController;

    @Autowired
    private JobController jobController;

    @Value("${tps-forvalteren.rest-api.url}")
    private String tpsfServerUrl;

    private Long gruppeId;
    private String miljoe;
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
        this.miljoe = "t9";
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
     * Scenario: Happypath Sjekker at systemet sender og mottar riktige verdier i post-requesten til hodejegeren og TPSF. Id-ene som
     * returneres fra hodejegeren skal sendes videre til TPSF gjennom metoden
     * {@link no.nav.registre.orkestratoren.consumer.rs.TpsfConsumer#sendSkdmeldingerTilTps}. Testen sjekker også at resultatet fra
     * TPSF stemmer overens med det som er forventet.
     */
    @Test
    public void shouldStartSyntetisering() {
        stubHodejegeren();
        stubTPSF();

        SyntetiserSkdmeldingerRequest ordreRequest = new SyntetiserSkdmeldingerRequest(gruppeId, miljoe, antallMeldingerPerEndringskode);

        AvspillingResponse avspillingResponse = syntetiseringsController.opprettSkdMeldinger(ordreRequest);

        assertEquals(expectedAntallSendte, avspillingResponse.getAntallSendte());
        assertEquals(expectedAntallFeilet, avspillingResponse.getAntallFeilet());
        assertEquals(expectedFoedselnummer, avspillingResponse.getStatusFraFeilendeMeldinger().get(0).getFoedselsnummer());
        assertEquals(expectedSekvensnummer, avspillingResponse.getStatusFraFeilendeMeldinger().get(0).getSekvensnummer());
        assertEquals(expectedStatus, avspillingResponse.getStatusFraFeilendeMeldinger().get(0).getStatus());

    }

    /**
     * Scenario: Tester at property-variablene blir henta riktig fra properties-fil når jobControlleren opprettes
     */
    @Test
    public void shouldGetProperties() {
        assertEquals(miljoe, jobController.getMiljoe());
        assertEquals(gruppeId, jobController.getSkdMeldingGruppeId());

        Map<String, Integer> testMap = new HashMap<>(jobController.getAntallMeldingerPerEndringskode());

        assertTrue(testMap.containsKey(endringskode1));
        assertEquals(antallEndringskode1, testMap.get(endringskode1).intValue());
        assertTrue(testMap.containsKey(endringskode2));
        assertEquals(antallEndringskode2, testMap.get(endringskode2).intValue());
    }

    public void stubHodejegeren() {
        stubFor(post(urlPathEqualTo("/api/v1/syntetisering/generer"))
                .withRequestBody(equalToJson("{\"gruppeId\": " + gruppeId
                        + ", \"miljoe\": \"" + miljoe
                        + "\", \"antallMeldingerPerEndringskode\": {\"" + endringskode1 + "\": " + antallMeldingerPerEndringskode.get(endringskode1) + "}}"))
                .willReturn(aResponse()
                        .withStatus(HttpStatus.CREATED.value())
                        .withHeader("Content-Type", "application/json")
                        .withBody("[" + expectedMeldingIds.get(0) + ", " + expectedMeldingIds.get(1) + "]")));
    }

    public void stubTPSF() {
        stubFor(post(urlPathEqualTo("/api/v1/endringsmelding/skd/send/" + gruppeId))
                .withRequestBody(equalToJson("{\"environment\": \"" + miljoe
                        + "\", \"ids\": [" + expectedMeldingIds.get(0) + ", " + expectedMeldingIds.get(1) + "]}"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("{\"antallSendte\": \"" + expectedAntallSendte
                                + "\", \"antallFeilet\": \"" + expectedAntallFeilet
                                + "\", \"statusFraFeilendeMeldinger\": [{\"foedselsnummer\": \"" + expectedFoedselnummer
                                + "\", \"sekvensnummer\": " + expectedSekvensnummer
                                + ", \"status\": \"" + expectedStatus + "\"}]}")));
    }
}
