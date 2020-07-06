package no.nav.registre.skd.consumer;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import no.nav.registre.skd.consumer.requests.SendToTpsRequest;
import no.nav.registre.skd.consumer.requests.SlettSkdmeldingerRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TpsfConsumerTest {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    private SendToTpsRequest sendToTpsRequest;

    private final long avspillergruppeId = 10L;
    private final String environment = "t9";
    private List<Long> ids;
    private int expectedAntallSendte;
    private int expectedAntallFeilet;
    private String expectedFoedselnummer;
    private Long expectedSekvensnummer;
    private String expectedStatus;

    /**
     * Scenario: Tester happypath til {@link TpsfConsumer#sendSkdmeldingerToTps}
     */
    @Test
    public void shouldSendSkdMeldingTilTpsf() {
        ids = new ArrayList<>();
        ids.add(123L);
        ids.add(321L);
        sendToTpsRequest = new SendToTpsRequest(environment, ids);

        expectedAntallSendte = 1;
        expectedAntallFeilet = 0;
        expectedFoedselnummer = "01010101010";
        expectedSekvensnummer = 10L;
        expectedStatus = "ok";

        stubTpsfConsumerSendSkdMelding();

        var response = tpsfConsumer.sendSkdmeldingerToTps(avspillergruppeId, sendToTpsRequest);

        assertEquals(expectedAntallSendte, response.getAntallSendte());
        assertEquals(expectedAntallFeilet, response.getAntallFeilet());
        assertEquals(expectedFoedselnummer, response.getStatusFraFeilendeMeldinger().get(0).getFoedselsnummer());
        assertEquals(expectedSekvensnummer, response.getStatusFraFeilendeMeldinger().get(0).getSekvensnummer());
        assertEquals(expectedStatus, response.getStatusFraFeilendeMeldinger().get(0).getStatus());
    }

    @Test
    public void shouldGetMeldingIdsTilhoerendeIdenterFromTpsf() {
        var identer = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));

        var expectedMeldingIds = new ArrayList<>(Arrays.asList(1L, 2L));

        stubTpsfConsumerGetMeldingIdsTilhoerendeIdenter(identer, expectedMeldingIds);

        var meldingIderTilhoerendeIdenter = tpsfConsumer.getMeldingIderTilhoerendeIdenter(avspillergruppeId, identer);
        assertThat(meldingIderTilhoerendeIdenter, hasSize(2));
        assertThat(meldingIderTilhoerendeIdenter, hasItems(expectedMeldingIds.get(0), expectedMeldingIds.get(1)));
    }

    @Test
    public void shouldDeleteMeldingerFromTpsf() {
        var meldingIder = new ArrayList<>(Arrays.asList(1L, 2L));
        var slettSkdmeldingerRequest = SlettSkdmeldingerRequest.builder()
                .ids(meldingIder)
                .build();
        stubTpsfConsumerDeleteMeldinger(slettSkdmeldingerRequest);

        var response = tpsfConsumer.slettMeldingerFraTpsf(meldingIder);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void shouldDeleteIdenterFromTps() {
        var identer = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));

        stubTpsfConsumerSlettIdenterFraTps(identer);

        var response = tpsfConsumer.slettIdenterFraTps(Collections.singletonList(environment), identer);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    private void stubTpsfConsumerSendSkdMelding() {
        stubFor(post(urlPathEqualTo("/tpsf/api/v1/endringsmelding/skd/send/" + avspillergruppeId))
                .withRequestBody(equalToJson(
                        "{\"environment\":\"" + environment
                                + "\",\"ids\":[" + ids.get(0) + ", " + ids.get(1) + "]}"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("{\"antallSendte\": \"" + expectedAntallSendte
                                + "\", \"antallFeilet\": \"" + expectedAntallFeilet
                                + "\", \"statusFraFeilendeMeldinger\": [{\"foedselsnummer\": \"" + expectedFoedselnummer
                                + "\", \"sekvensnummer\": " + expectedSekvensnummer
                                + ", \"status\": \"" + expectedStatus + "\"}]}")));
    }

    private void stubTpsfConsumerGetMeldingIdsTilhoerendeIdenter(List<String> identer, List<Long> expectedMeldingIds) {
        stubFor(post(urlPathEqualTo("/tpsf/api/v1/endringsmelding/skd/meldinger/" + avspillergruppeId))
                .withRequestBody(equalToJson(
                        "[\"" + identer.get(0) + "\", \"" + identer.get(1) + "\"]"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("[" + expectedMeldingIds.get(0) + ", " + expectedMeldingIds.get(1) + "]")));
    }

    private void stubTpsfConsumerDeleteMeldinger(SlettSkdmeldingerRequest slettSkdmeldingerRequest) {
        stubFor(post(urlPathEqualTo("/tpsf/api/v1/endringsmelding/skd/deletemeldinger"))
                .withRequestBody(equalToJson(
                        "{\"ids\": [" + slettSkdmeldingerRequest.getIds().get(0) + ", " + slettSkdmeldingerRequest.getIds().get(1) + "]}"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")));
    }

    private void stubTpsfConsumerSlettIdenterFraTps(List<String> identer) {
        stubFor(delete(urlEqualTo("/tpsf/api/v1/endringsmelding/skd/deleteFromTps?miljoer=" + environment + "&identer=" + identer.get(0) + "," + identer.get(1)))
                .willReturn(ok())
        );
    }
}
