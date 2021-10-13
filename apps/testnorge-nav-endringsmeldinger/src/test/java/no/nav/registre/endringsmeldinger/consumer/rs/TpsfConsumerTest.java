package no.nav.registre.endringsmeldinger.consumer.rs;

import no.nav.registre.endringsmeldinger.consumer.rs.requests.SendTilTpsRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TpsfConsumerTest {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    private SendTilTpsRequest sendTilTpsRequest;
    private String fnr = "01010101010";
    private String miljoe = "t1";
    private String ko = "testko";
    private Long timeout = 5L;
    private String melding;

    @Before
    public void setUp() {
        melding = "<sfePersonData>"
                + "    <sfeAjourforing>"
                + "        <systemInfo>"
                + "            <kilde>PP01</kilde>"
                + "            <brukerID>Srvpselv</brukerID>"
                + "        </systemInfo>"
                + "        <endreTelefon>"
                + "            <offentligIdent></offentligIdent>"
                + "            <typeTelefon>MOBI</typeTelefon>"
                + "            <telefonNr>69328480</telefonNr>"
                + "            <datoTelefon>2017-10-30</datoTelefon>"
                + "        </endreTelefon>"
                + "    </sfeAjourforing>"
                + "</sfePersonData>";

        sendTilTpsRequest = SendTilTpsRequest.builder()
                .miljoe(miljoe)
                .melding(melding)
                .ko(ko)
                .timeout(timeout)
                .build();
    }

    @Test
    public void shouldSendEndringsmeldingTilTps() {
        stubTpsfConsumer();

        var response = tpsfConsumer.sendEndringsmeldingTilTps(sendTilTpsRequest);

        assertThat(response.getXml(), containsString(fnr));
    }

    private void stubTpsfConsumer() {
        stubFor(post(urlPathEqualTo("/tpsf/api/v1/xmlmelding"))
                .withRequestBody(equalToJson(
                        "{\n"
                                + "  \"miljoe\": \"" + miljoe + "\",\n"
                                + "  \"melding\": \"" + melding + "\",\n"
                                + "  \"ko\": \"" + ko + "\",\n"
                                + "  \"timeout\": " + timeout + "\n"
                                + "}"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("\"<sfePersonData>"
                                + "    <sfeAjourforing>"
                                + "        <systemInfo>"
                                + "            <kilde>PP01</kilde>"
                                + "            <brukerID>Srvpselv</brukerID>"
                                + "        </systemInfo>"
                                + "        <endreTelefon>"
                                + "            <offentligIdent>" + fnr + "</offentligIdent>"
                                + "            <typeTelefon>MOBI</typeTelefon>"
                                + "            <telefonNr>69328480</telefonNr>"
                                + "            <datoTelefon>2017-10-30</datoTelefon>"
                                + "        </endreTelefon>"
                                + "    </sfeAjourforing>"
                                + "</sfePersonData>\"")));
    }
}