package no.nav.registre.testnorge.arena.provider.rs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.registre.testnorge.arena.consumer.rs.response.aktoer.AktoerInnhold;
import no.nav.registre.testnorge.arena.consumer.rs.response.aktoer.AktoerResponse;
import no.nav.registre.testnorge.arena.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.tilleggsstoenad.Vedtaksperiode;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTillegg;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.libs.test.JsonWiremockHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class TilleggsstoenadControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ident = "01019049900";
    private static final String miljoe = "test";
    private static final Long avspillergruppeId = 123456789L;

    private static final String tokenProviderUrl = "(.*)/token-provider";
    private static final String hentLevendeIdenterUrl = "(.*)/hodejegeren/api/v1/alle-levende-identer/" + avspillergruppeId;
    private static final String hentIdenterIAktoerregisteretUrl = "(.*)/aktoerregister/v1/identer";
    private static final String arenaForvalterenUrl = "(.*)/arena-forvalteren/api/v1/bruker";
    private static final String arenaTilleggBoutgiftUrl = "(.*)/synt-tillegg/api/v1/arena/tilleggsstonad/boutgift";
    private static final String opprettRettigheterTilleggUrl = "(.*)/arena-forvalteren/api/v1/tilleggsstonad";
    private static final String opprettRettigheterTiltaksaktivitetUrl = "(.*)/arena-forvalteren/api/v1/tiltaksaktivitet";
    private static final String arenaTiltaksaktivitetUrl = "(.*)/synt-tiltak/api/v1/arena/tiltak/aktivitet";

    private SyntetiserArenaRequest syntetiserArenaRequest;
    private Map<String, AktoerResponse> identerIAktoerRegisteret;
    private NyttVedtakTillegg vedtakTillegg;
    private NyttVedtakTiltak vedtakTiltak;
    private NyeBrukereResponse hentBrukereResponse;
    private NyeBrukereResponse sendBrukerResponse;
    private NyttVedtakResponse nyttVedtakTilleggResponse;
    private NyttVedtakResponse nyttVedtakTiltakResponse;

    @Before
    public void setUp() {
        syntetiserArenaRequest = new SyntetiserArenaRequest(avspillergruppeId, miljoe, 1);

        identerIAktoerRegisteret = new HashMap<>();
        var aktoerResponse = new AktoerResponse();
        aktoerResponse.setIdenter(Collections.singletonList(new AktoerInnhold(ident, "test", true)));
        identerIAktoerRegisteret.put(ident, aktoerResponse);

        vedtakTillegg = new NyttVedtakTillegg();
        vedtakTillegg.setVedtaksperiode(new Vedtaksperiode(LocalDate.now().minusMonths(3), LocalDate.now()));
        vedtakTillegg.setRettighetKode("TSOBOUTG");

        vedtakTiltak = new NyttVedtakTiltak();
        vedtakTiltak.setFraDato(LocalDate.now().minusMonths(3));
        vedtakTiltak.setTilDato(LocalDate.now());

        hentBrukereResponse = new NyeBrukereResponse();
        hentBrukereResponse.setAntallSider(0);
        hentBrukereResponse.setArbeidsoekerList(Collections.emptyList());

        sendBrukerResponse = new NyeBrukereResponse();
        sendBrukerResponse.setAntallSider(0);
        sendBrukerResponse.setArbeidsoekerList(Collections.singletonList(Arbeidsoeker.builder()
                .personident(ident)
                .miljoe(miljoe)
                .build()));

        nyttVedtakTilleggResponse = NyttVedtakResponse.builder()
                .nyeRettigheterTillegg(Collections.singletonList(vedtakTillegg))
                .feiledeRettigheter(Collections.emptyList())
                .build();

        nyttVedtakTiltakResponse = NyttVedtakResponse.builder()
                .nyeRettigheterTiltak(Collections.singletonList(vedtakTiltak))
                .feiledeRettigheter(Collections.emptyList())
                .build();
    }

    @Test
    public void shouldGenerereTilleggBoutgifter() throws Exception {

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tokenProviderUrl)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaTilleggBoutgiftUrl)
                .withResponseBody(Collections.singletonList(vedtakTillegg))
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentLevendeIdenterUrl)
                .withResponseBody(Collections.singletonList(ident))
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentIdenterIAktoerregisteretUrl)
                .withResponseBody(identerIAktoerRegisteret)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaForvalterenUrl)
                .withResponseBody(hentBrukereResponse)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaForvalterenUrl)
                .withResponseBody(sendBrukerResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaTiltaksaktivitetUrl)
                .withResponseBody(Collections.singletonList(vedtakTiltak))
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(opprettRettigheterTilleggUrl)
                .withResponseBody(nyttVedtakTilleggResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(opprettRettigheterTiltaksaktivitetUrl)
                .withResponseBody(nyttVedtakTiltakResponse)
                .stubPost();

        var mvcResultat = mvc.perform(post("/api/v1/syntetisering/generer/tillegg/boutgifter")
                .content(objectMapper.writeValueAsString(syntetiserArenaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Map<String, List<NyttVedtakResponse>> resultat = objectMapper.readValue(mvcResultat,
                new TypeReference<>() {
                });

        assertThat(resultat.keySet()).contains(ident).hasSize(1);
    }

    @AfterEach
    public void cleanUp() {
        reset();
    }
}
