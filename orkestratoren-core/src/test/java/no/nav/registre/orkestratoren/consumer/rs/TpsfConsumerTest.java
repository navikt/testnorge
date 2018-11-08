package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.consumer.rs.requests.SendToTpsRequest;
import no.nav.registre.orkestratoren.consumer.rs.response.AvspillingResponse;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class TpsfConsumerTest {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    private SendToTpsRequest sendToTpsRequest;

    private long gruppeId = 10L;
    private String environment = "t9";
    private List<Long> ids;
    private int expectedAntallSendte;
    private int expectedAntallFeilet;
    private String expectedFoedselnummer;
    private Long expectedSekvensnummer;
    private String expectedStatus;

    /**
     * Scenario: Tester happypath til {@link TpsfConsumer#sendSkdmeldingerTilTps}
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

        stubTpsfConsumer();

        AvspillingResponse response = tpsfConsumer.sendSkdmeldingerTilTps(gruppeId, sendToTpsRequest);

        assertEquals(expectedAntallSendte, response.getAntallSendte());
        assertEquals(expectedAntallFeilet, response.getAntallFeilet());
        assertEquals(expectedFoedselnummer, response.getStatusFraFeilendeMeldinger().get(0).getFoedselsnummer());
        assertEquals(expectedSekvensnummer, response.getStatusFraFeilendeMeldinger().get(0).getSekvensnummer());
        assertEquals(expectedStatus, response.getStatusFraFeilendeMeldinger().get(0).getStatus());
    }

    public void stubTpsfConsumer() {
        stubFor(post(urlPathEqualTo("/api/v1/endringsmelding/skd/send/" + gruppeId))
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
}
