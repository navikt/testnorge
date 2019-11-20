package no.nav.registre.orkestratoren.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.rtv.namespacetps.TpsPersonDokumentType;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

@RunWith(SpringRunner.class)
@RestClientTest(HodejegerenConsumer.class)
@ActiveProfiles("test")
public class HodejegerenConsumerTest {

    @Autowired
    private HodejegerenConsumer hodejegerenConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-hodejegeren.rest.api.url}")
    private String serverUrl;

    private TpsPersonDokumentType tpsPersonDokument;
    private String fnr = "01010101010";
    private Long avspillergruppeId = 123L;
    private String miljoe = "q2";

    @Before
    public void setUp() {
        tpsPersonDokument = new TpsPersonDokumentType();
    }

    @Test
    public void shouldSendPersondokumentTilHodejegeren() throws JsonProcessingException {
        var expectedUri = serverUrl + "/v1/historikk/skd/oppdaterDokument/{ident}";
        stubSendPersondokument(expectedUri);

        var identer = hodejegerenConsumer.sendTpsPersondokumentTilHodejegeren(tpsPersonDokument, fnr);

        assertThat(identer, contains(fnr));
    }

    @Test
    public void shouldHenteAlleIdenter() {
        var expectedUri = serverUrl + "/v1/alle-identer/{avspillergruppeId}";
        stubHentAlleIdenter(expectedUri);

        var identer = hodejegerenConsumer.hentAlleIdenter(avspillergruppeId);

        assertThat(identer, contains(fnr));
    }

    @Test
    public void shouldFinneIdenterSomIkkeErITps() {
        var expectedUri = serverUrl + "/v1/identer-ikke-i-tps/{avspillergruppeId}?miljoe={miljoe}";
        stubHentIdenterIkkeITps(expectedUri);

        var identer = hodejegerenConsumer.hentIdenterSomIkkeErITps(avspillergruppeId, miljoe);

        assertThat(identer, contains(fnr));
    }

    @Test
    public void shouldFinneIdenterSomKollidererITps() {
        var expectedUri = serverUrl + "/v1/identer-som-kolliderer/{avspillergruppeId}";
        stubHentIdenterSomKolliderer(expectedUri);

        var identer = hodejegerenConsumer.hentIdenterSomKollidererITps(avspillergruppeId);

        assertThat(identer, contains(fnr));
    }

    @Test
    public void shouldOppdatereHodejegerenCache() {
        var expectedUri = serverUrl + "/v1/cache/oppdaterGruppe/{avspillergruppeId}";
        stubOppdaterCache(expectedUri);

        hodejegerenConsumer.oppdaterHodejegerenCache(avspillergruppeId);
    }

    private void stubSendPersondokument(String expectedUri) throws JsonProcessingException {
        server.expect(requestToUriTemplate(expectedUri, fnr))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(asJsonString(tpsPersonDokument)))
                .andRespond(withSuccess("[\"" + fnr + "\"]", MediaType.APPLICATION_JSON));
    }

    private void stubHentAlleIdenter(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri, avspillergruppeId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[\"" + fnr + "\"]", MediaType.APPLICATION_JSON));
    }

    private void stubHentIdenterIkkeITps(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri, avspillergruppeId, miljoe))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[\"" + fnr + "\"]", MediaType.APPLICATION_JSON));
    }

    private void stubHentIdenterSomKolliderer(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri, avspillergruppeId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[\"" + fnr + "\"]", MediaType.APPLICATION_JSON));
    }

    private void stubOppdaterCache(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri, avspillergruppeId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("Avspillergruppe " + avspillergruppeId + " ble oppdatert", MediaType.APPLICATION_JSON));
    }

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }
}