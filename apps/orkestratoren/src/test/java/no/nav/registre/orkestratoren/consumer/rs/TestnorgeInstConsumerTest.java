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
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;

@ExtendWith(MockitoExtension.class)
@RestClientTest(TestnorgeInstConsumer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TestnorgeInstConsumerTest {

    private static final Long AVSPILLERGRUPPE_ID = 123L;
    private static final String MILJOE = "q2";
    @Autowired
    private TestnorgeInstConsumer testnorgeInstConsumer;
    @Autowired
    private MockRestServiceServer server;
    @Value("${testnorge-inst.rest.api.url}")
    private String serverUrl;
    private SyntetiserInstRequest syntetiserInstRequest;
    private List<String> identer;

    @BeforeEach
    public void setUp() {
        identer = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));
        syntetiserInstRequest = new SyntetiserInstRequest(AVSPILLERGRUPPE_ID, MILJOE, identer.size());
    }

    @Test
    public void shouldStartSyntetisering() {
        var expectedUri = serverUrl + "/v1/syntetisering/generer?miljoe={miljoe}";
        stubInstConsumerStartSyntetisering(expectedUri);

        var response = (ResponseEntity) testnorgeInstConsumer.startSyntetisering(syntetiserInstRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    @Test
    public void shouldDeleteIdenterFromInst() {
        var expectedUri = serverUrl + "/v1/ident/batch?miljoe={miljoe}&identer={identer}";
        stubInstConsumerSlettIdenter(expectedUri);

        var response = testnorgeInstConsumer.slettIdenterFraInst(identer);

        assertThat(response.getInstStatus().get(0).getPersonident(), equalTo(identer.get(0)));
        assertThat(response.getInstStatus().get(0).getStatus(), equalTo(HttpStatus.OK));
        assertThat(response.getInstStatus().get(1).getPersonident(), equalTo(identer.get(1)));
        assertThat(response.getInstStatus().get(1).getStatus(), equalTo(HttpStatus.NOT_FOUND));
    }

    private void stubInstConsumerStartSyntetisering(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri, MILJOE))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\":" + AVSPILLERGRUPPE_ID
                        + ",\"miljoe\":\"" + MILJOE + "\""
                        + ",\"antallNyeIdenter\":" + identer.size() + "}"))
                .andRespond(withSuccess());
    }

    private void stubInstConsumerSlettIdenter(String expectedUri) {
        var identerAsString = String.join(",", identer);
        server.expect(requestToUriTemplate(expectedUri, MILJOE, identerAsString))
                .andExpect(method(HttpMethod.DELETE))
                .andRespond(withSuccess("[\n"
                        + "  {\n"
                        + "    \"personident\": \"" + identer.get(0) + "\",\n"
                        + "    \"status\": \"OK\",\n"
                        + "    \"institusjonsopphold\": {\n"
                        + "      \"oppholdId\": 200485356,\n"
                        + "      \"tssEksternId\": \"80000465396\",\n"
                        + "      \"institusjonstype\": \"AS\",\n"
                        + "      \"varighet\": \"L\",\n"
                        + "      \"kategori\": \"A\",\n"
                        + "      \"startdato\": \"2018-08-12\",\n"
                        + "      \"faktiskSluttdato\": \"2019-04-13\",\n"
                        + "      \"forventetSluttdato\": \"2019-04-13\",\n"
                        + "      \"kilde\": \"PP01\",\n"
                        + "      \"overfoert\": false\n"
                        + "    },\n"
                        + "    \"feilmelding\": null\n"
                        + "  },\n"
                        + "  {\n"
                        + "    \"personident\": \"" + identer.get(1) + "\",\n"
                        + "    \"status\": \"NOT_FOUND\",\n"
                        + "    \"institusjonsopphold\": null,\n"
                        + "    \"feilmelding\": \"Fant ingen institusjonsopphold på ident.\"\n"
                        + "  }\n"
                        + "]", MediaType.APPLICATION_JSON));
    }
}