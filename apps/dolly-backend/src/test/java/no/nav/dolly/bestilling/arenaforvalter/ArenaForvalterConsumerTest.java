package no.nav.dolly.bestilling.arenaforvalter;

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

import com.fasterxml.jackson.databind.JsonNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

import no.nav.dolly.config.credentials.ArenaforvalterProxyProperties;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaArbeidssokerBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyBruker;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukere;
import no.nav.dolly.domain.resultset.arenaforvalter.ArenaNyeBrukereResponse;
import no.nav.dolly.errorhandling.ErrorStatusDecoder;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
public class ArenaForvalterConsumerTest {

    private static final String IDENT = "12345678901";
    private static final String ENV = "q2";

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenService;

    @MockBean
    private ErrorStatusDecoder errorStatusDecoder;

    @Autowired
    private ArenaForvalterConsumer arenaForvalterConsumer;

    @BeforeEach
    public void setup() {

        when(tokenService.exchange(ArgumentMatchers.any(ArenaforvalterProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
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
