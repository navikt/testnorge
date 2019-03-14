package no.nav.registre.hodejegeren.consumer;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@RestClientTest(TpsfConsumer.class)
@ActiveProfiles("itest")
public class TpsfConsumerTest {

    @Autowired
    private TpsfConsumer tpsfConsumer;
    @Autowired
    private MockRestServiceServer server;
    @Value("${tps-forvalteren.rest-api.url}")
    private String serverUrl;

    /**
     * Tester om konsumenten bygger korrekt URI og queryParam.
     * Og mottar et Set med String, med de rette elementene i.
     *
     * @throws IOException
     */
    @Test
    public void shouldSendCorrectRequestParametersAndGetIdenterFiltrertPaaAarsakskode() {
        long gruppeId = 123L;
        String transaksjonskode = "1";
        Set<String> expectedIdenter = new HashSet<>();
        expectedIdenter.add("12345678901");
        expectedIdenter.add("12345678902");

        String expectedUri = serverUrl + "/v1/endringsmelding/skd/identer/{gruppeId}?"
                + "aarsakskode={aarsakskode}&transaksjonstype={transaksjonstype}";
        this.server.expect(requestToUriTemplate(expectedUri, gruppeId, "01,02", transaksjonskode))
                .andRespond(withSuccess("[\"12345678901\",\"12345678902\"]", MediaType.APPLICATION_JSON));

        Set<String> identer = tpsfConsumer.getIdenterFiltrertPaaAarsakskode(gruppeId, Arrays.asList("01", "02"), transaksjonskode);
        assertEquals(2, identer.size());
        assertEquals(expectedIdenter, identer);
    }

    /**
     * Tester om konsumenten bygger korrekt URI og queryParam.
     *
     * @throws IOException
     */
    @Test
    public void shouldWriteProperRequestWhenGettingTpsServiceRoutine() throws IOException {
        String aksjonskode = "B0";
        String environment = "env";
        String fnr = "bla";

        String rutinenavn = "a";
        String expectedUri = serverUrl + "/v1/serviceroutine/{routineName}?aksjonsKode={aksjonskode}&environment={environment}&fnr={fnr}";

        this.server.expect(requestToUriTemplate(expectedUri, rutinenavn, aksjonskode, environment, fnr))
                .andRespond(request -> new MockClientHttpResponse("[]".getBytes(), HttpStatus.OK));

        tpsfConsumer.getTpsServiceRoutine(rutinenavn, aksjonskode, environment, fnr);
    }
}