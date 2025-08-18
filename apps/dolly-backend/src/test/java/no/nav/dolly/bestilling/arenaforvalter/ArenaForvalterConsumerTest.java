package no.nav.dolly.bestilling.arenaforvalter;

import no.nav.dolly.bestilling.AbstractConsumerTest;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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


class ArenaForvalterConsumerTest extends AbstractConsumerTest {

    private static final String IDENT = "12345678901";
    private static final String ENV = "q2";

    @Autowired
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @Test
    void deleteIdent() {

        stubGetMiljoe();
        stubDeleteArenaForvalterBruker();

        /*var response = */
        arenaForvalterConsumer.deleteIdenter(List.of(IDENT)).collectList()
                .as(StepVerifier::create)
                .assertNext(arenaBrukerList ->
                        assertThat(arenaBrukerList.getFirst().getStatus(), is(HttpStatus.OK)))
                .verifyComplete();

        verify(tokenExchange).exchange(ArgumentMatchers.any(ServerProperties.class));
    }

    @Test
    void postArenadata() {

        stubPostArenaForvalterBruker();

        arenaForvalterConsumer.postArenaBruker(ArenaNyeBrukere.builder()
                        .nyeBrukere(singletonList(ArenaNyBruker.builder().personident(IDENT).build()))
                        .build())
                .collectList()
                .as(StepVerifier::create)
                .assertNext(response -> {
                    assertThat(response.getFirst().getStatus(), is(HttpStatus.CREATED));
                    assertThat(response.getFirst().getNyBrukerFeilList(), is(emptyList()));
                });
    }

    @Test
    void getIdent_OK() {

        stubGetArenaForvalterBruker();

        StepVerifier.create(arenaForvalterConsumer.getArenaBruker(IDENT, ENV))
                .expectNextMatches(arenaBruker ->
                        arenaBruker.getStatus().is2xxSuccessful())
                .verifyComplete();
    }

    private void stubGetMiljoe() {

        stubFor(get(urlPathMatching("(.*)/api/v1/miljoe"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("[\"" + ENV + "\"]")));
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
