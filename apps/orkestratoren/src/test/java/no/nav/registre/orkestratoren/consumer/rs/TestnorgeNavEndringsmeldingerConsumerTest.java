package no.nav.registre.orkestratoren.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.HashMap;
import java.util.Map;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserNavmeldingerRequest;

@ExtendWith(MockitoExtension.class)
@RestClientTest(TestnorgeNavEndringsmeldingerConsumer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TestnorgeNavEndringsmeldingerConsumerTest {

    @Autowired
    private TestnorgeNavEndringsmeldingerConsumer testnorgeNavEndringsmeldingerConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-nav-endringsmeldinger.rest.api.url}")
    private String serverUrl;

    private final long gruppeId = 10L;
    private final String miljoe = "t9";
    private final String endringskode = "Z010";
    private final int antallPerEndringskode = 2;
    private SyntetiserNavmeldingerRequest syntetiserNavmeldingerRequest;

    @BeforeEach
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
        var expectedUri = serverUrl + "/v1/syntetisering/generer";
        stubNavSyntConsumer(expectedUri);

        var response = testnorgeNavEndringsmeldingerConsumer.startSyntetisering(syntetiserNavmeldingerRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    private void stubNavSyntConsumer(String expectedUri) {
        var fnr = "01010101010";
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\":" + gruppeId
                        + ",\"miljoe\":\"" + miljoe
                        + "\",\"antallMeldingerPerEndringskode\":{\"" + endringskode + "\":" + antallPerEndringskode + "}}"))
                .andRespond(withSuccess("[\"<sfePersonData>"
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
                        + "</sfePersonData>\"]", MediaType.APPLICATION_JSON));
    }
}