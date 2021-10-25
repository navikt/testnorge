package no.nav.registre.orkestratoren.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import no.nav.testnav.libs.domain.dto.namespacetps.TpsPersonDokumentType;

@RestClientTest(HodejegerenHistorikkConsumer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class HodejegerenHistorikkConsumerTest {

    @Autowired
    private HodejegerenHistorikkConsumer hodejegerenHistorikkConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-hodejegeren.rest.api.url}")
    private String serverUrl;

    private TpsPersonDokumentType tpsPersonDokument;
    private final String fnr = "01010101010";
    private final Long avspillergruppeId = 123L;

    private static String asJsonString(final Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsString(object);
    }

    @BeforeEach
    public void setUp() {
        tpsPersonDokument = new TpsPersonDokumentType();
    }

    @Test
    public void shouldSendPersondokumentTilHodejegeren() throws JsonProcessingException {
        var expectedUri = serverUrl + "/v1/historikk/skd/oppdaterDokument/{ident}";
        stubSendPersondokument(expectedUri);

        var identer = hodejegerenHistorikkConsumer.sendTpsPersondokumentTilHodejegeren(tpsPersonDokument, fnr);

        assertThat(identer, contains(fnr));
    }

    @Test
    public void shouldOppdatereHodejegerenCache() {
        var expectedUri = serverUrl + "/v1/cache/oppdaterGruppe/{avspillergruppeId}";
        stubOppdaterCache(expectedUri);

        hodejegerenHistorikkConsumer.oppdaterHodejegerenCache(avspillergruppeId);
    }

    private void stubSendPersondokument(String expectedUri) throws JsonProcessingException {
        server.expect(requestToUriTemplate(expectedUri, fnr))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(asJsonString(tpsPersonDokument)))
                .andRespond(withSuccess("[\"" + fnr + "\"]", MediaType.APPLICATION_JSON));
    }

    private void stubOppdaterCache(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri, avspillergruppeId))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("Avspillergruppe " + avspillergruppeId + " ble oppdatert", MediaType.APPLICATION_JSON));
    }
}