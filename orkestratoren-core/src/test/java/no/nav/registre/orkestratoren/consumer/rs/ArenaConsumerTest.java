package no.nav.registre.orkestratoren.consumer.rs;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserArenaRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalToJson;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@ActiveProfiles("test")
public class ArenaConsumerTest {

    @Autowired
    private ArenaConsumer arenaConsumer;

    private static final Long AVSPILLERGRUPPE_ID = 10L;
    private static final String MILJOE = "t1";
    private SyntetiserArenaRequest syntetiserArenaRequest;
    private List<String> identer;
    private List<String> arbeidsokere;

    private String fnr1 = "10101010101";
    private String fnr2 = "20202020202";
    private String fnr3 = "30303030303";

    @Before
    public void setUp() {
        identer = new ArrayList<>(Arrays.asList(fnr1, fnr2, fnr3));
        syntetiserArenaRequest = new SyntetiserArenaRequest(AVSPILLERGRUPPE_ID, MILJOE, 2);
    }

    @Test
    public void shouldSendTilArenaForvalteren() {
        stubArenaConsumerLeggTilIdenter();

        int antallOpprettede = arenaConsumer.opprettArbeidsokere(syntetiserArenaRequest);
        assertThat(antallOpprettede, is(2));
    }



    private void stubArenaConsumerLeggTilIdenter() {
        stubFor(post(urlPathEqualTo("/arena/api/v1/generer"))
                .withRequestBody(equalToJson(
                        "{\"avspillergruppeId\": " + AVSPILLERGRUPPE_ID +
                                ", \"miljoe\": \"" + MILJOE + "\"" +
                                ", \"antallNyeIdenter\": " + identer.size() + "}"
                ))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("" + identer.size())));
    }

    private void stubArenaConsumerSlettIdenter() {
        stubFor(delete(urlPathEqualTo("/arena/api/v1/slett?miljoe=" + MILJOE))
        .withRequestBody(equalToJson("[\"" + identer.get(1) + "\",\"" + identer.get(2) + "\"]"))
                .willReturn(ok()
                .withHeader("Content-Type", "application/json")
                .withBody(
                        "{ \"slettet\": [\"" + identer.get(1) + "\",\"" + identer.get(2) + "\"],\"ikkeSlettet\": [] }"
                )));
    }

}
