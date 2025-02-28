package no.nav.dolly.bestilling.arenaforvalter;

import no.nav.dolly.domain.resultset.arenaforvalter.ArenaBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.elastic.BestillingElasticRepository;
import no.nav.dolly.libs.test.DollySpringBootTest;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.securitycore.domain.ServerProperties;
import no.nav.testnav.libs.standalone.servletsecurity.exchange.TokenExchange;
import org.hamcrest.CoreMatchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@DollySpringBootTest
@AutoConfigureWireMock(port = 0)
class ArenaForvalterConsumerTest {

    private static final String IDENT = "12345678901";
    private static final String ENV = "q2";

    @MockitoBean
    private TokenExchange tokenService;

    @MockitoBean
    private AccessToken accessToken;

    @Autowired
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @MockitoBean
    private BestillingElasticRepository bestillingElasticRepository;

    @MockitoBean
    private ElasticsearchOperations elasticsearchOperations;

    @BeforeEach
    void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(ServerProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    void deleteIdent() {

        stubDeleteArenaForvalterBruker();

        /*var response = */
        arenaForvalterConsumer.deleteIdenter(List.of(IDENT)).collectList().block();

        verify(tokenService).exchange(ArgumentMatchers.any(ServerProperties.class));
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

        stubFor(get(urlPathMatching("(.*)/arenaforvalter/" + ENV + "/arena/syntetiser/brukeroppfolging/personstatusytelse"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"status\":\"OK\"}")));
    }
}
