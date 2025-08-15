package no.nav.dolly.bestilling.arenaforvalter;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.bestilling.AbstractConsumerTest;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

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

@ExtendWith(MockitoExtension.class)
class ArenaForvalterConsumerTest extends AbstractConsumerTest {

    private static final String IDENT = "12345678901";
    private static final String ENV = "q2";

    private ArenaForvalterConsumer arenaForvalterConsumer;

    @BeforeEach
    void setup() {

        when(consumers.getTestnavArenaForvalterenProxy()).thenReturn(serverProperties);
        arenaForvalterConsumer = new ArenaForvalterConsumer(consumers, tokenExchange, new ObjectMapper(), webClient);
    }

    @Test
    void deleteIdent() {

        stubDeleteArenaForvalterBruker();

        /*var response = */
        arenaForvalterConsumer.deleteIdenter(List.of(IDENT)).collectList().block();

        verify(tokenExchange).exchange(ArgumentMatchers.any(ServerProperties.class));
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

        assertThat(response.getFirst().getArbeidsokerList().getFirst().getStatus(), is(CoreMatchers.equalTo(ArenaBruker.BrukerStatus.OK)));
        assertThat(response.getFirst().getNyBrukerFeilList(), is(emptyList()));
    }

    @Test
    void getIdent_OK() {

        stubGetArenaForvalterBruker();

        StepVerifier.create(arenaForvalterConsumer.getArenaBruker(IDENT, ENV))
                .expectNextMatches(arenaBruker ->
                        arenaBruker.getStatus().is2xxSuccessful())
                .verifyComplete();

//        assertThat("Response should be 200 successful", response.getStatus().is2xxSuccessful());
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

        stubFor(get(urlPathMatching("(.*)/arenaforvalter/" + ENV + "/arena/syntetiser/brukeroppfolging/personstatusytelse"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"OK\"}")));
    }
}
