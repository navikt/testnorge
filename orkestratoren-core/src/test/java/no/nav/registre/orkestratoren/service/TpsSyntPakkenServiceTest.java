package no.nav.registre.orkestratoren.service;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import no.nav.registre.orkestratoren.consumer.rs.HodejegerenConsumer;
import org.springframework.web.client.HttpStatusCodeException;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class TpsSyntPakkenServiceTest {

    @Autowired
    TpsSyntPakkenService tpsSyntPakkenService;

    private long gruppeId = 10L;
    private String miljoe = "t9";
    private String endringskode = "0110";
    private int antallPerEndringskode = 2;
    private List<Long> expectedMeldingsIds;
    private Map<String, Integer> antallMeldingerPerEndringskode;

    @Before
    public void setUp() {
        antallMeldingerPerEndringskode = new HashMap<>();
        antallMeldingerPerEndringskode.put(endringskode, antallPerEndringskode);
        expectedMeldingsIds = new ArrayList<>();
        expectedMeldingsIds.add(120421016L);
        expectedMeldingsIds.add(110156008L);
    }

    /**
     * Scenario: HVIS hodejegeren returnerer et exception, skal metoden {@link HodejegerenConsumer#startSyntetisering} hente ut
     * id-ene som ligger i exception og returnere disse slik at de kan lagres i TPS.
     */
    @Test(expected = HttpStatusCodeException.class)
    public void shouldReturnIdsWhenReceivingException() {
        stubHodejegerenConsumerWithError();
        stubTpsfConsumer();

        tpsSyntPakkenService.produserOgSendSkdmeldingerTilTpsIMiljoer(gruppeId, miljoe, antallMeldingerPerEndringskode);

        verify(postRequestedFor(urlPathEqualTo("/tpsf/api/v1/endringsmelding/skd/send/" + gruppeId))
                .withRequestBody(equalToJson(
                        "{\"environment\" : \"" + miljoe
                                + "\", \"ids\" : [" + expectedMeldingsIds.get(0) + ", " + expectedMeldingsIds.get(1) + "]}")));
    }

    public void stubHodejegerenConsumerWithError() {
        stubFor(post(urlPathEqualTo("/hodejegeren/api/v1/syntetisering/generer"))
                .withRequestBody(equalToJson(
                        "{\"gruppeId\":" + gruppeId
                                + ",\"miljoe\":\"" + miljoe
                                + "\",\"antallMeldingerPerEndringskode\":{\"" + endringskode + "\":" + antallPerEndringskode + "}}"))
                .willReturn(aResponse()
                        .withStatus(500)
                        .withBody("{\"ids\": [" + expectedMeldingsIds.get(0) + ", " + expectedMeldingsIds.get(1) + "]}")));
    }

    public void stubTpsfConsumer() {
        stubFor(post(urlPathEqualTo("/tpsf/api/v1/endringsmelding/skd/send/" + gruppeId))
                .willReturn(ok()));
    }
}
