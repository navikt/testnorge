package no.nav.registre.orkestratoren.consumer.rs;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;

@ExtendWith(MockitoExtension.class)
@RestClientTest(TestnorgeArenaConsumer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TestnorgeArenaConsumerTest {

    private static final Long AVSPILLERGRUPPE_ID = 10L;
    private static final String MILJOE = "t1";
    @Autowired
    private TestnorgeArenaConsumer testnorgeArenaConsumer;
    @Autowired
    private MockRestServiceServer server;
    @Value("${testnorge-arena.rest.api.url}")
    private String serverUrl;
    private SyntetiserArenaRequest syntetiserArenaRequest;
    private List<String> identer;

    private final String fnr1 = "10101010101";
    private final String fnr2 = "20202020202";
    private final String fnr3 = "30303030303";

    @BeforeEach
    public void setUp() {
        identer = new ArrayList<>(Arrays.asList(fnr1, fnr2, fnr3));
        syntetiserArenaRequest = new SyntetiserArenaRequest(AVSPILLERGRUPPE_ID, MILJOE, 2);
    }

    @Test
    public void shouldSendTilArenaForvalteren() {
        var expectedUri = serverUrl + "/v1/syntetisering/generer";
        stubArenaConsumerLeggTilIdenter(expectedUri);

        var opprettedeIdenter = testnorgeArenaConsumer.opprettArbeidsoekere(syntetiserArenaRequest, false);
        assertThat(opprettedeIdenter.size(), is(2));
        assertThat(opprettedeIdenter.get(1), containsString(fnr3));
    }

    @Test
    public void shouldDeleteIdenter() {
        var expectedUri = serverUrl + "/v1/ident/slett?miljoe={miljoe}";
        stubArenaConsumerSlettIdenter(expectedUri);

        var identerToBeSlettet = Arrays.asList(fnr2, fnr3);
        var response = testnorgeArenaConsumer.slettIdenter(MILJOE, identerToBeSlettet);
        assertThat(response.getSlettet().size(), is(2));
        assertThat(response.getIkkeSlettet().size(), is(0));
        assertThat(response.getSlettet().get(0), containsString(fnr2));
    }

    private void stubArenaConsumerLeggTilIdenter(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\": " + AVSPILLERGRUPPE_ID +
                        ", \"miljoe\": \"" + MILJOE + "\"" +
                        ", \"antallNyeIdenter\": 2}"))
                .andRespond(withSuccess("{\"registrerteIdenter\": [\"" + fnr2 + "\",\"" + fnr3 + "\"]}", MediaType.APPLICATION_JSON));
    }

    private void stubArenaConsumerSlettIdenter(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri, MILJOE))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(content().json("[\"" + identer.get(1) + "\",\"" + identer.get(2) + "\"]"))
                .andRespond(withSuccess("{ \"slettet\": [\"" + identer.get(1) + "\",\"" + identer.get(2) + "\"],\"ikkeSlettet\": [] }", MediaType.APPLICATION_JSON));
    }
}
