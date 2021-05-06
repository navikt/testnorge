package no.nav.registre.hodejegeren.consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@RunWith(SpringRunner.class)
@RestClientTest(TpsfConsumer.class)
@ActiveProfiles("itest")
@TestPropertySource(locations = "classpath:application-itest.properties")
public class TpsfConsumerTest {

    @Autowired
    private TpsfConsumer tpsfConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${tps-forvalteren.rest-api.url}")
    private String serverUrl;

    private long avspillergruppeId = 123L;

    /**
     * Tester om konsumenten bygger korrekt URI og queryParam.
     * Og mottar et Set med String, med de rette elementene i.
     */
    @Test
    public void shouldSendCorrectRequestParametersAndGetIdenterFiltrertPaaAarsakskode() {
        var transaksjonskode = "1";
        Set<String> expectedIdenter = new HashSet<>();
        expectedIdenter.add("12345678901");
        expectedIdenter.add("12345678902");

        var expectedUri = serverUrl + "/v1/endringsmelding/skd/identer/{avspillergruppeId}?"
                + "aarsakskode={aarsakskode}&transaksjonstype={transaksjonstype}";
        this.server.expect(requestToUriTemplate(expectedUri, avspillergruppeId, "01,02", transaksjonskode))
                .andRespond(withSuccess("[\"12345678901\",\"12345678902\"]", MediaType.APPLICATION_JSON));

        var identer = tpsfConsumer.getIdenterFiltrertPaaAarsakskode(avspillergruppeId, Arrays.asList("01", "02"), transaksjonskode);
        assertThat(identer, hasSize(2));
        assertThat(identer, IsIterableContainingInOrder.contains(expectedIdenter.toArray()));
    }

    /**
     * Tester om konsumenten bygger korrekt URI og queryParam.
     *
     * @throws IOException
     */
    @Test
    public void shouldWriteProperRequestWhenGettingTpsServiceRoutine() throws IOException {
        var aksjonskode = "B0";
        var miljoe = "env";
        var fnr = "bla";

        var rutinenavn = "a";
        var expectedUri = serverUrl + "/v1/serviceroutine/{routineName}?aksjonsKode={aksjonskode}&environment={miljoe}&fnr={fnr}";

        this.server.expect(requestToUriTemplate(expectedUri, rutinenavn, aksjonskode, miljoe, fnr))
                .andRespond(request -> new MockClientHttpResponse("[]".getBytes(), HttpStatus.OK));

        tpsfConsumer.getTpsServiceRoutine(rutinenavn, aksjonskode, miljoe, fnr);
    }
}