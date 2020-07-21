package no.nav.registre.arena.core.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.registre.arena.core.consumer.rs.response.AktoerInnhold;
import no.nav.registre.arena.core.consumer.rs.response.AktoerResponse;
import no.nav.registre.arena.core.pensjon.response.HttpStatus;
import no.nav.registre.arena.core.pensjon.response.PensjonTestdataResponse;
import no.nav.registre.arena.core.pensjon.response.PensjonTestdataResponseDetails;
import no.nav.registre.arena.core.pensjon.response.PensjonTestdataStatus;
import no.nav.registre.arena.core.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakAap;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyttVedtakResponse;
import no.nav.registre.testnorge.test.JsonWiremockHelper;
import org.junit.Before;
import org.junit.jupiter.api.AfterEach;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class AapControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ident = "01019049900";
    private static final String miljoe = "test";
    private static final Long avspillergruppeId=123456789L;

    private NyttVedtakAap vedtak;
    private NyttVedtakResponse nyttVedtakResponse;
    private NyeBrukereResponse nyeBrukereResponse;
    private Map<String, AktoerResponse> identerIAktoerRegisteret;
    private PensjonTestdataResponse pensjonTestdataResponse;

    @Before
    public void setUp(){
        vedtak = NyttVedtakAap.builder().aktivitetsfase("TEST").build();
        vedtak.setFraDato(LocalDate.now().minusMonths(3));
        vedtak.setTilDato(LocalDate.now());

        nyttVedtakResponse = NyttVedtakResponse.builder()
                .nyeRettigheterAap(Collections.singletonList(vedtak))
                .feiledeRettigheter(Collections.emptyList())
                .build();

        nyeBrukereResponse = new NyeBrukereResponse();
        nyeBrukereResponse.setAntallSider(0);
        nyeBrukereResponse.setArbeidsoekerList(Collections.emptyList());

        identerIAktoerRegisteret = new HashMap<>();
        var aktoerResponse = new AktoerResponse();
        aktoerResponse.setIdenter(Collections.singletonList(new AktoerInnhold(ident,"test", true)));
        identerIAktoerRegisteret.put(ident, aktoerResponse );

        pensjonTestdataResponse = PensjonTestdataResponse.builder()
                .status(Collections.singletonList(PensjonTestdataStatus.builder()
                        .miljo(miljoe)
                        .response(PensjonTestdataResponseDetails.builder()
                                .httpStatus(new HttpStatus("test", 200))
                                .build())
                        .build()))
                .build();

    }

    @Test
    public void shouldGenerereRettighetAap() throws Exception {
        var tokenProviderUrl="(.*)/token-provider";
        var hentLevendeIdenterIAldersgruppeUrl="(.*)/hodejegeren/api/v1/levende-identer-i-aldersgruppe/"+avspillergruppeId;
        var hentIdenterIAktoerregisteretUrl="(.*)/aktoerregister/v1/identer";
        var brukereArenaUrl="(.*)/arena-forvalteren/api/v1/bruker";
        var hentSyntetisertRettighetAapUrl="(.*)/syntetisereren/api/v1/arena/aap";
        var hentSyntetisertRettighetAap115Url="(.*)/syntetisereren/api/v1/arena/aap/11_5";
        var opprettPersonIPoppUrl="(.*)/pensjon-testdata-facade/api/v1/person";
        var opprettInntektIPoppUrl="(.*)/pensjon-testdata-facade/api/v1/inntekt";
        var opprettRettigheterAap115Url = "(.*)/arena-forvalteren/api/v1/aap115";
        var opprettRettigheterAapUrl = "(.*)/arena-forvalteren/api/v1/aap";
        var saveHistorikkUrl="(.*)/hodejegeren/api/v1/historikk";

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tokenProviderUrl)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentLevendeIdenterIAldersgruppeUrl)
                .withResponseBody(Collections.singletonList(ident))
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentIdenterIAktoerregisteretUrl)
                .withResponseBody(identerIAktoerRegisteret)
                .stubGet();

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

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentSyntetisertRettighetAapUrl)
                .withResponseBody(Collections.singletonList(vedtak))
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentSyntetisertRettighetAap115Url)
                .withResponseBody(Collections.singletonList(vedtak))
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(opprettPersonIPoppUrl)
                .withResponseBody(pensjonTestdataResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(opprettInntektIPoppUrl)
                .withResponseBody(pensjonTestdataResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(opprettRettigheterAap115Url)
                .withResponseBody(nyttVedtakResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(opprettRettigheterAapUrl)
                .withResponseBody(nyttVedtakResponse)
                .stubPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(saveHistorikkUrl)
                .stubPost();

        mvc.perform(post("/api/v1/syntetisering/generer/rettighet/aap")
                .content(objectMapper.writeValueAsString(new SyntetiserArenaRequest(avspillergruppeId, miljoe, 1)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(tokenProviderUrl)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentLevendeIdenterIAldersgruppeUrl)
                .withResponseBody(Collections.singletonList(ident))
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentIdenterIAktoerregisteretUrl)
                .withResponseBody(identerIAktoerRegisteret)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(brukereArenaUrl)
                .withResponseBody(nyeBrukereResponse)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(brukereArenaUrl)
                .withResponseBody(nyeBrukereResponse)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentSyntetisertRettighetAapUrl)
                .withResponseBody(Collections.singletonList(vedtak))
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentSyntetisertRettighetAap115Url)
                .withResponseBody(Collections.singletonList(vedtak))
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(opprettPersonIPoppUrl)
                .withResponseBody(pensjonTestdataResponse)
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(opprettInntektIPoppUrl)
                .withResponseBody(pensjonTestdataResponse)
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(opprettRettigheterAap115Url)
                .withResponseBody(nyttVedtakResponse)
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(opprettRettigheterAapUrl)
                .withResponseBody(nyttVedtakResponse)
                .verifyPost();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(saveHistorikkUrl)
                .verifyPost();
    }

    @AfterEach
    public void tearDown() {
        reset();
    }
}