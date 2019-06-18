package no.nav.registre.hodejegeren.consumer;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
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
import org.springframework.http.ResponseEntity;
import org.springframework.mock.http.client.MockClientHttpResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
        String environment = "env";
        String fnr = "bla";

        String rutinenavn = "a";
        String expectedUri = serverUrl + "/v1/serviceroutine/{routineName}?aksjonsKode={aksjonskode}&environment={environment}&fnr={fnr}";

        this.server.expect(requestToUriTemplate(expectedUri, rutinenavn, aksjonskode, environment, fnr))
                .andRespond(request -> new MockClientHttpResponse("[]".getBytes(), HttpStatus.OK));

        tpsfConsumer.getTpsServiceRoutine(rutinenavn, aksjonskode, environment, fnr);
    }

    @Test
    public void shouldGetMeldingIdsFromTpsf() {
        List<String> identer = new ArrayList<>();
        identer.add("12345678901");
        identer.add("12345678902");

        Long expectedMeldingId1 = 1L;
        Long expectedMeldingId2 = 2L;

        String expectedUri = serverUrl + "/v1/endringsmelding/skd/meldinger/{avspillergruppeId}";
        this.server.expect(requestToUriTemplate(expectedUri, avspillergruppeId))
                .andRespond(withSuccess("[" + expectedMeldingId1 + ", " + expectedMeldingId2 + "]", MediaType.APPLICATION_JSON));

        List<Long> meldingIderTilhoerendeIdenter = tpsfConsumer.getMeldingIderTilhoerendeIdenter(avspillergruppeId, identer);
        assertThat(meldingIderTilhoerendeIdenter, hasSize(2));
        assertThat(meldingIderTilhoerendeIdenter, hasItems(expectedMeldingId1, expectedMeldingId2));
    }

    @Test
    public void shouldDeleteMeldingerFromTpsf() {
        List<Long> meldingIder = new ArrayList<>(Arrays.asList(1L, 2L));
        String expectedUri = serverUrl + "/v1/endringsmelding/skd/deletemeldinger";
        this.server.expect(requestToUriTemplate(expectedUri))
                .andRespond(withSuccess());

        ResponseEntity response = tpsfConsumer.slettMeldingerFraTpsf(meldingIder);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }
}