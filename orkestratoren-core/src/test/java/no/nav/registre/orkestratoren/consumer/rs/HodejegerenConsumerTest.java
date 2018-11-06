package no.nav.registre.orkestratoren.consumer.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class HodejegerenConsumerTest {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    private long gruppeId = 10L;
    private String miljoe = "t9";
    private String endringskode = "0110";
    private int antallPerEndringskode = 1;
    private List<Long> expectedMeldingsIds;

    @Test
    public void shouldStartSyntetisering() {
        HashMap<String, Integer> antallMeldingerPerAarsakskode = new HashMap<>();
        antallMeldingerPerAarsakskode.put(endringskode, antallPerEndringskode);
        GenereringsOrdreRequest ordreRequest = new GenereringsOrdreRequest(gruppeId, miljoe, antallMeldingerPerAarsakskode);

        expectedMeldingsIds = new ArrayList<>();
        expectedMeldingsIds.add(120421016L);
        expectedMeldingsIds.add(110156008L);

        stubHodejegerenConsumer();

        List<Long> ids = hodejegerenConsumer.startSyntetisering(ordreRequest);

        assertEquals(expectedMeldingsIds.toString(), ids.toString());
    }

    public void stubHodejegerenConsumer() {
        stubFor(post(urlPathEqualTo("/api/v1/syntetisering/generer"))
                .withRequestBody(equalToJson(
                        "{\"gruppeId\":" + gruppeId
                                + ",\"miljoe\":\"" + miljoe
                                + "\",\"antallMeldingerPerEndringskode\":{\"" + endringskode + "\":" + antallPerEndringskode + "}}"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[" + expectedMeldingsIds.get(0) + ", " + expectedMeldingsIds.get(1) + "]")));
    }
}
