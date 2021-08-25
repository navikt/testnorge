package no.nav.dolly.bestilling.arenaforvalter;

import com.fasterxml.jackson.databind.JsonNode;
import com.github.tomakehurst.wiremock.client.WireMock;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.properties.ProvidersProps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;
import static wiremock.org.hamcrest.MatcherAssert.assertThat;

@ActiveProfiles("test")
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-test.yaml")
@AutoConfigureWireMock(port = 0)
public class ArenaForvalterConsumerTest {

    private static final String IDENT = "12423353";
    private static final String ENV = "u2";

    @Mock
    private ProvidersProps providersProps;

    @Autowired
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @Before
    public void setup() {

        WireMock.reset();
        when(providersProps.getArenaForvalter()).thenReturn(ProvidersProps.ArenaForvalter.builder().url("baseUrl").build());
    }

    @Test
    public void deleteIdent() {

        stubDeleteArenaForvalterBruker();

        ResponseEntity<JsonNode> response = arenaForvalterConsumer.deleteIdent(IDENT, ENV);
        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }


    @Test
    public void postArenadata() {

        stubPostArenaForvalterBruker();

        ResponseEntity<ArenaNyeBrukereResponse> response =
                arenaForvalterConsumer.postArenadata(ArenaNyeBrukere.builder()
                        .nyeBrukere(singletonList(ArenaNyBruker.builder().personident(IDENT).build()))
                        .build());

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    @Test
    public void getIdent_OK() {

        stubGetArenaForvalterBruker();

        ResponseEntity<ArenaArbeidssokerBruker> response = arenaForvalterConsumer.getIdent(IDENT);

        assertThat("Response should be 200 successful", response.getStatusCode().is2xxSuccessful());
    }

    private void stubDeleteArenaForvalterBruker() {

        stubFor(delete(urlPathMatching("(.*)/arenaforvalter/api/v1/bruker"))
                .withQueryParam("miljoe", equalTo(ENV))
                .withQueryParam("personident", equalTo(IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubPostArenaForvalterBruker() {

        stubFor(post(urlPathMatching("(.*)/arenaforvalter/api/v1/bruker"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubGetArenaForvalterBruker() {

        stubFor(get(urlPathMatching("(.*)/arenaforvalter/api/v1/bruker"))
                .withQueryParam("filter-personident", equalTo(IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));
    }
}
