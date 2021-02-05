package no.nav.registre.testnorge.arena.provider.rs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.registre.testnorge.arena.consumer.rs.response.AktoerInnhold;
import no.nav.registre.testnorge.arena.consumer.rs.response.AktoerResponse;
import no.nav.registre.testnorge.arena.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.response.KontoinfoResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakTiltak;

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
public class TiltakControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ident = "01019049900";
    private static final String miljoe = "test";
    private static final Long avspillergruppeId = 123456789L;

    private static final String tokenProviderUrl = "(.*)/token-provider";
    private static final String hentIdenterMedKontonummerUrl = "(.*)/hodejegeren/api/v1/identer-med-kontonummer/" + avspillergruppeId;
    private static final String hentLevendeIdenterUrl = "(.*)/hodejegeren/api/v1/alle-levende-identer/" + avspillergruppeId;
    private static final String hentIdenterIAktoerregisteretUrl = "(.*)/aktoerregister/v1/identer";
    private static final String hentBrukereArenaUrl = "(.*)/arena-forvalteren/api/v1/bruker";
    private static final String arenaTiltakspengerUrl = "(.*)/synt-tiltak/api/v1/arena/tiltak/basi";
    private static final String arenaTiltaksdeltakelseUrl = "(.*)/synt-tiltak/api/v1/arena/tiltak/deltakelse/1";
    private static final String arenaEndreDeltakerstatusUrl = "(.*)/synt-tiltak/api/v1/arena/tiltak/deltakerstatus";
    private static final String opprettRettigheterTiltaksdeltakelseUrl = "(.*)/arena-forvalteren/api/v1/tiltaksdeltakelse";
    private static final String opprettRettigheterTiltaksaktivitetUrl = "(.*)/arena-forvalteren/api/v1/tiltakspenger";
    private static final String opprettRettigheterEndreDeltakerstatusUrl = "(.*)/arena-forvalteren/api/v1/endreDeltakerstatus";
    private static final String brukereArenaUrl = "(.*)/arena-forvalteren/api/v1/bruker";

    private SyntetiserArenaRequest syntetiserArenaRequest;
    private Map<String, AktoerResponse> identerIAktoerRegisteret;
    private NyeBrukereResponse nyeBrukereResponse;
    private NyttVedtakTiltak vedtakTiltak;
    private NyttVedtakResponse nyttVedtakTiltakResponse;

    @Before
    public void setUp() {
        syntetiserArenaRequest = new SyntetiserArenaRequest(avspillergruppeId, miljoe, 1);

        identerIAktoerRegisteret = new HashMap<>();
        var aktoerResponse = new AktoerResponse();
        aktoerResponse.setIdenter(Collections.singletonList(new AktoerInnhold(ident, "test", true)));
        identerIAktoerRegisteret.put(ident, aktoerResponse);

        nyeBrukereResponse = new NyeBrukereResponse();
        nyeBrukereResponse.setAntallSider(0);
        nyeBrukereResponse.setArbeidsoekerList(Collections.emptyList());

        vedtakTiltak = new NyttVedtakTiltak();
        vedtakTiltak.setFraDato(LocalDate.now().minusMonths(3));
        vedtakTiltak.setTilDato(LocalDate.now());
        vedtakTiltak.setTiltakskarakteristikk("TEST");
        vedtakTiltak.setDeltakerstatusKode("FULLF");

        nyttVedtakTiltakResponse = NyttVedtakResponse.builder()
                .nyeRettigheterTiltak(Collections.singletonList(vedtakTiltak))
                .feiledeRettigheter(Collections.emptyList())
                .build();

    }

    @Test
    public void shouldGenerereTiltakspenger() throws Exception {

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tokenProviderUrl)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentIdenterMedKontonummerUrl)
                .withResponseBody(Collections.singletonList(KontoinfoResponse.builder()
                        .fnr(ident)
                        .adresseLinje1("Linje 1")
                        .fornavn("Hans")
                        .etternavn("Hansen")
                        .kontonummer("123")
                        .build()))
                .stubGet();

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
                .withUrlPathMatching(hentBrukereArenaUrl)
                .withResponseBody(nyeBrukereResponse)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaTiltakspengerUrl)
                .withResponseBody(Collections.singletonList(vedtakTiltak))
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaTiltaksdeltakelseUrl)
                .withResponseBody(Collections.singletonList(vedtakTiltak))
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(opprettRettigheterTiltaksdeltakelseUrl)
                .withResponseBody(nyttVedtakTiltakResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaEndreDeltakerstatusUrl)
                .withResponseBody(Collections.singletonList(vedtakTiltak))
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(opprettRettigheterEndreDeltakerstatusUrl)
                .withResponseBody(nyttVedtakTiltakResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(opprettRettigheterTiltaksaktivitetUrl)
                .withResponseBody(nyttVedtakTiltakResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(brukereArenaUrl)
                .withResponseBody(nyeBrukereResponse)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(brukereArenaUrl)
                .withResponseBody(nyeBrukereResponse)
                .stubPost();

        var mvcResultat = mvc.perform(post("/api/v1/syntetisering/generer/tiltakspenger")
                .content(objectMapper.writeValueAsString(syntetiserArenaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaTiltakspengerUrl)
                .withResponseBody(Collections.singletonList(vedtakTiltak))
                .verifyPost();

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
