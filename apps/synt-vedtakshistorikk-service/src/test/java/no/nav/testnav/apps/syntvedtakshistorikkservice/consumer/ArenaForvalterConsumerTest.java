package no.nav.testnav.apps.syntvedtakshistorikkservice.consumer;

import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.credential.ArenaForvalterenProxyProperties;
import no.nav.testnav.apps.syntvedtakshistorikkservice.consumer.request.arena.rettighet.*;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static no.nav.testnav.apps.syntvedtakshistorikkservice.utils.ResourceUtils.getResourceFileContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
class ArenaForvalterConsumerTest {

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private TokenExchange tokenExchange;

    @Autowired
    private ArenaForvalterConsumer arenaForvalterConsumer;

    private final String miljoe = "q2";
    private final String fnr = "270699494213";

    @BeforeEach
    public void setup() {
        when(tokenExchange.exchange(ArgumentMatchers.any(ArenaForvalterenProxyProperties.class))).thenReturn(Mono.just(new AccessToken("token")));
    }

    @Test
    void checkExceptionOccursOnBadSentTilArenaForvalterRequest() {
        stubOpprettErrorResponse();
        assertThrows(Exception.class, () -> {
            arenaForvalterConsumer.sendBrukereTilArenaForvalter(null);
        });

    }

    private void stubOpprettErrorResponse() {
        stubFor(post(urlPathMatching("(.*)/arena/api/v1/bruker"))
                .willReturn(aResponse().withStatus(500))
        );
    }

    @Test
    void hentBrukereTest() {
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
    void getBrukereFilterTest() {
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
    void getEmptyResponse() {
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

    @Test
    void shouldOppretteRettighetAap() {
        var aapRequest = new RettighetAapRequest();
        aapRequest.setPersonident(fnr);
        var aap115Request = new RettighetAap115Request();
        aap115Request.setPersonident(fnr);
        var ungUfoerRequest = new RettighetUngUfoerRequest();
        ungUfoerRequest.setPersonident(fnr);
        var tvungenForvaltningRequest = new RettighetTvungenForvaltningRequest();
        tvungenForvaltningRequest.setPersonident(fnr);
        var fritakMeldekortRequest = new RettighetFritakMeldekortRequest();
        fritakMeldekortRequest.setPersonident(fnr);

        var rettigheter = new ArrayList<>(Arrays.asList(
                aapRequest,
                aap115Request,
                ungUfoerRequest,
                tvungenForvaltningRequest,
                fritakMeldekortRequest
        ));
        stubArenaForvalterOpprettAapRettighet();

        var response = arenaForvalterConsumer.opprettRettighet(rettigheter);

        assertThat(response.get(fnr).get(0).getNyeRettigheterAap()).hasSize(1);
        assertThat(response.get(fnr).get(1).getNyeRettigheterAap()).hasSize(1);
        assertThat(response.get(fnr).get(2).getNyeRettigheterAap()).hasSize(1);
        assertThat(response.get(fnr).get(3).getNyeRettigheterAap()).hasSize(1);
        assertThat(response.get(fnr).get(4).getFeiledeRettigheter()).hasSize(1);
    }

    private void stubArenaForvalterOpprettAapRettighet() {
        stubFor(post(urlEqualTo("/arena/api/v1/aap"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/aap/aap_forvalter_response.json"))
                )
        );

        stubFor(post(urlEqualTo("/arena/api/v1/aap115"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/aap/aap115_forvalter_response.json"))
                )
        );

        stubFor(post(urlEqualTo("/arena/api/v1/aapungufor"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/aap/ung_ufoer_forvalter_response.json"))
                )
        );

        stubFor(post(urlEqualTo("/arena/api/v1/aaptvungenforvaltning"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/aap/tvungen_forvaltning_forvalter_response.json"))
                )
        );

        stubFor(post(urlEqualTo("/arena/api/v1/aapfritakmeldekort"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/aap/fritak_meldekort_forvalter_response.json"))
                )
        );
    }

    @Test
    void shouldOppretteRettighetTillegg() {
        var tilleggRequest = new RettighetTilleggRequest();
        tilleggRequest.setPersonident(fnr);
        List<RettighetRequest> rettigheter = new ArrayList<>(Collections.singletonList(
                tilleggRequest
        ));

        stubArenaForvalterOpprettTilleggRettighet();

        var response = arenaForvalterConsumer.opprettRettighet(rettigheter);

        assertThat(response.get(fnr).get(0).getNyeRettigheterTillegg()).hasSize(1);
    }

    private void stubArenaForvalterOpprettTilleggRettighet() {
        stubFor(post(urlEqualTo("/arena/api/v1/tilleggsstonad"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/tillegg/tillegg_forvalter_response.json"))
                )
        );
    }

    @Test
    void shouldOppretteRettighetTiltak() {
        var tiltaksdeltakelseRequest = new RettighetTiltaksdeltakelseRequest();
        tiltaksdeltakelseRequest.setPersonident(fnr);
        var tiltakspengerRequest = new RettighetTiltakspengerRequest();
        tiltakspengerRequest.setPersonident(fnr);

        var rettigheter = new ArrayList<>(Arrays.asList(
                tiltaksdeltakelseRequest,
                tiltakspengerRequest
        ));

        stubArenaForvalterOpprettTiltakRettighet();

        var response = arenaForvalterConsumer.opprettRettighet(rettigheter);

        assertThat(response.get(fnr).get(0).getFeiledeRettigheter()).isEmpty();
        assertThat(response.get(fnr).get(1).getNyeRettigheterTiltak()).hasSize(1);
    }

    private void stubArenaForvalterOpprettTiltakRettighet() {
        stubFor(post(urlEqualTo("/arena/api/v1/tiltaksdeltakelse"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/tiltak/tiltaksdeltakelse_forvalter_response.json"))
                )
        );

        stubFor(post(urlEqualTo("/arena/api/v1/tiltakspenger"))
                .willReturn(ok()
                        .withHeader("Content-Type", "application/json")
                        .withBody(getResourceFileContent("files/arena/tiltak/tiltakspenger_forvalter_response.json"))
                )
        );
    }

}

