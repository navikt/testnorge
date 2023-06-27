package no.nav.dolly.bestilling.arenaforvalter;

import no.nav.dolly.config.credentials.ArenaforvalterProxyProperties;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.delete;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
class ArenaForvalterConsumerTest {

    private static final String IDENT = "12345678901";
    private static final String ENV = "q2";

    @MockBean
    private TokenExchange tokenService;

    @MockBean
    private AccessToken accessToken;

    @Autowired
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @BeforeEach
    void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(ArenaforvalterProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    void deleteIdent() {

        stubDeleteArenaForvalterBruker();

        var response = arenaForvalterConsumer.deleteIdenter(List.of(IDENT)).collectList().block();

        verify(tokenService).exchange(ArgumentMatchers.any(ArenaforvalterProxyProperties.class));
    }

    @Test
    void postArenadata() {

        stubPostArenaForvalterBruker();

        var response =
                arenaForvalterConsumer.postArenaBruker(ArenaNyeBrukere.builder()
                                .nyeBrukere(singletonList(ArenaNyBruker.builder().personident(IDENT).build()))
                                .build())
                        .collectList()
                        .block();

        assertThat(response.get(0).getArbeidsokerList().get(0).getStatus(), is(CoreMatchers.equalTo(ArenaBruker.BrukerStatus.OK)));
        assertThat(response.get(0).getNyBrukerFeilList(), is(emptyList()));
    }

    @Test
    void getIdent_OK() {

        stubGetArenaForvalterBruker();

        var response = arenaForvalterConsumer.getArenaBruker(IDENT, ENV).block();

        assertThat("Response should be 200 successful", response.getStatus().is2xxSuccessful());
    }

    private void stubDeleteArenaForvalterBruker() {

        stubFor(delete(urlPathMatching("(.*)/arenaforvalter/api/v1/bruker"))
                .withQueryParam("miljoe", equalTo(ENV))
                .withQueryParam("personident", equalTo(IDENT))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")));

        stubFor(get(urlPathMatching("(.*)/api/v1/miljoe"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + ENV + "\"]")));
    }

    private void stubPostArenaForvalterBruker() {

        stubFor(post(urlPathMatching("(.*)/arenaforvalter/api/v1/bruker"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"arbeidsokerList\":[{\"status\":\"OK\"}]}")));
    }

    private void stubGetArenaForvalterBruker() {

        stubFor(get(urlPathMatching("(.*)/arenaforvalter/q2/arena/syntetiser/brukeroppfolging/personstatusytelse"))
                .withQueryParam("filter-personident", equalTo(IDENT))
                .withQueryParam("filter-miljoe", equalTo(ENV))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"OK\"}")));
    }
}
