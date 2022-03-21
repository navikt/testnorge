package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.ArenaForvalterenProxyProperties;
import no.nav.testnav.libs.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.testnav.libs.securitycore.domain.AccessToken;
import no.nav.testnav.libs.servletsecurity.exchange.TokenExchange;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.utils.ResourceUtils.getResourceFileContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
public class ArenaForvalterConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenExchange;

    @Autowired
    private ArenaForvalterConsumer arenaForvalterConsumer;

    private final String miljoe = "q2";

    @BeforeEach
    public void setup() {
        when(tokenExchange.exchange(ArgumentMatchers.any(ArenaForvalterenProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

//    @Test(expected = Exception.class)
//    public void checkExceptionOccursOnBadSentTilArenaForvalterRequest() {
//       arenaForvalterConsumer.sendBrukereTilArenaForvalter(null);
//    }

    @Test
    public void hentBrukereTest() {
        stubHentBrukere();

        List<Arbeidsoeker> response = arenaForvalterConsumer.hentArbeidsoekere("", "", "");

        assertThat(response.get(2).getPersonident()).isEqualTo("09838817873");
        assertThat(response).hasSize(3);

    }

    private void stubHentBrukere() {
        stubFor(get(urlEqualTo("/arena/api/v1/bruker"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/arena_brukere_page_0.json"))
                )
        );
        stubFor(get(urlEqualTo("/arena/api/v1/bruker?page=0"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/arena_brukere_page_0.json"))
                )
        );
        stubFor(get(urlEqualTo("/arena/api/v1/bruker?page=1"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/arena_brukere_page_1.json"))
                )
        );
    }

    @Test
    public void getBrukereFilterTest() {
        stubHentBrukereFilter();
        List<Arbeidsoeker> response = arenaForvalterConsumer.hentArbeidsoekere("10101010101", "Dolly", miljoe);

        assertThat(response.get(0).getStatus()).isEqualTo("OK");
        assertThat(response).hasSize(2);
        assertThat(response.get(1).getAap()).isTrue();
    }


    private void stubHentBrukereFilter() {
        stubFor(get(urlEqualTo("/arena/api/v1/bruker?filter-eier=Dolly&filter-miljoe=q2&filter-personident=10101010101"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/all_filters_page_0.json"))
                )
        );
        stubFor(get(urlEqualTo("/arena/api/v1/bruker?filter-eier=Dolly&filter-miljoe=q2&filter-personident=10101010101&page=0"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/all_filters_page_0.json"))
                )
        );
        stubFor(get(urlEqualTo("/arena/api/v1/bruker?filter-eier=Dolly&filter-miljoe=q2&filter-personident=10101010101&page=1"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/all_filters_page_1.json"))
                )
        );
    }

    @Test
    public void getEmptyResponse() {
        stubEmptyResponse();
        List<Arbeidsoeker> response = arenaForvalterConsumer.hentArbeidsoekere("10101010101", "", miljoe);
        assertThat(response).isEmpty();
    }

    private void stubEmptyResponse() {
        stubFor(get(urlEqualTo("/arena/api/v1/bruker?filter-miljoe=q2&filter-personident=10101010101"))
                .willReturn(aResponse().withStatus(400)
                        .withBody("{" +
                                "\"timestamp\": \"2019-07-03T07:45:19.109+0000\"," +
                                "\"status\": 400," +
                                "\"error\": \"Bad Request\"," +
                                "\"message\": \"Identen er ikke registrert i arena-forvalteren\"," +
                                "\"path\": \"/api/v1/bruker\"" +
                                "}")
                )
        );
    }

}

