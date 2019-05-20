package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

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

import no.nav.registre.orkestratoren.consumer.rs.response.RsPureXmlMessageResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserNavmeldingerRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class NavSyntConsumerTest {

    @Autowired
    private NavSyntConsumer navSyntConsumer;

    private long gruppeId = 10L;
    private String miljoe = "t9";
    private String endringskode = "Z010";
    private int antallPerEndringskode = 2;
    private String fnr = "01010101010";
    private SyntetiserNavmeldingerRequest syntetiserNavmeldingerRequest;

    @Before
    public void setUp() {
        Map<String, Integer> antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put(endringskode, antallPerEndringskode);
        syntetiserNavmeldingerRequest = SyntetiserNavmeldingerRequest.builder()
                .avspillergruppeId(gruppeId)
                .miljoe(miljoe)
                .antallMeldingerPerEndringskode(antallMeldingerPerEndringskode)
                .build();
    }

    @Test
    public void shouldStartSyntetisering() {
        stubNavSyntConsumer();

        ResponseEntity<List<RsPureXmlMessageResponse>> response = navSyntConsumer.startSyntetisering(syntetiserNavmeldingerRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    private void stubNavSyntConsumer() {
        stubFor(post(urlPathEqualTo("/nav/api/v1/syntetisering/generer"))
                .withRequestBody(equalToJson(
                        "{\"avspillergruppeId\":" + gruppeId
                                + ",\"miljoe\":\"" + miljoe
                                + "\",\"antallMeldingerPerEndringskode\":{\"" + endringskode + "\":" + antallPerEndringskode + "}}"))
                .willReturn(ok()
                        .withHeader("content-type", "application/json")
                        .withBody("[\"<sfePersonData>"
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
                                + "</sfePersonData>\"]")));
    }
}