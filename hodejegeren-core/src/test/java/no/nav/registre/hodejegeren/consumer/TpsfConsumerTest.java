package no.nav.registre.hodejegeren.consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
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
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
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

    private long avspillergruppeId = 123L;

    /**
     * Tester om konsumenten bygger korrekt URI og queryParam.
     * Og mottar et Set med String, med de rette elementene i.
     */
    @Test
    public void shouldSendCorrectRequestParametersAndGetIdenterFiltrertPaaAarsakskode() {
        String transaksjonskode = "1";
        Set<String> expectedIdenter = new HashSet<>();
        expectedIdenter.add("12345678901");
        expectedIdenter.add("12345678902");

        String expectedUri = serverUrl + "/v1/endringsmelding/skd/identer/{avspillergruppeId}?"
                + "aarsakskode={aarsakskode}&transaksjonstype={transaksjonstype}";
        this.server.expect(requestToUriTemplate(expectedUri, avspillergruppeId, "01,02", transaksjonskode))
                .andRespond(withSuccess("[\"12345678901\",\"12345678902\"]", MediaType.APPLICATION_JSON));

        Set<String> identer = tpsfConsumer.getIdenterFiltrertPaaAarsakskode(avspillergruppeId, Arrays.asList("01", "02"), transaksjonskode);
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
        String aksjonskode = "B0";
        String miljoe = "env";
        String fnr = "bla";

        String rutinenavn = "a";
        String expectedUri = serverUrl + "/v1/serviceroutine/{routineName}?aksjonsKode={aksjonskode}&environment={miljoe}&fnr={fnr}";

        this.server.expect(requestToUriTemplate(expectedUri, rutinenavn, aksjonskode, miljoe, fnr))
                .andRespond(request -> new MockClientHttpResponse("[]".getBytes(), HttpStatus.OK));

        tpsfConsumer.getTpsServiceRoutine(rutinenavn, aksjonskode, miljoe, fnr);
    }

    /**
     * Tester om konsumenten bygger korrekt URI og path-variabel.
     * Og mottar en liste med Long, med de rette elementene i.
     */
    @Test
    public void shouldWriteProperRequestAndGetMeldingIds() {
        Long avspillergruppeId = 123L;
        String fnr = "01010101010";
        List<String> fnrs = Collections.singletonList(fnr);
        String expectedUri = serverUrl + "/v1/endringsmelding/skd/meldinger/{avspillergruppeId}";
        Long expectedMeldingId = 234L;

        this.server.expect(requestToUriTemplate(expectedUri, avspillergruppeId))
                .andRespond(withSuccess("[" + expectedMeldingId + "]", MediaType.APPLICATION_JSON));

        List<Long> meldingIderTilhoerendeIdenter = tpsfConsumer.getMeldingIderTilhoerendeIdenter(this.avspillergruppeId, fnrs);

        assertThat(meldingIderTilhoerendeIdenter, containsInAnyOrder(expectedMeldingId));
    }

    /**
     * Tester om konsumenten bygger korrekt URI og request-body.
     */
    @Test
    public void shouldWriteProperRequestAndDeleteMeldinger() {
        String expectedUri = serverUrl + "/v1/endringsmelding/skd/deletemeldinger";
        Long expectedMeldingId = 234L;
        List<Long> expectedMeldingIds = Collections.singletonList(expectedMeldingId);

        this.server.expect(requestToUriTemplate(expectedUri))
                .andExpect(content().json("{\"ids\":[" + expectedMeldingIds.get(0) + "]}"))
                .andRespond(withSuccess());

        tpsfConsumer.slettMeldingerFraTpsf(expectedMeldingIds);
    }
}