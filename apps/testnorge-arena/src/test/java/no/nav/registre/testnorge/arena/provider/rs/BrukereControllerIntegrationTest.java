package no.nav.registre.testnorge.arena.provider.rs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.testnorge.arena.provider.rs.request.SyntetiserArenaRequest;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
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

import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
public class BrukereControllerIntegrationTest {
    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ident = "01019049900";
    private static final String miljoe = "test";
    private static final Long avspillergruppeId = 123456789L;

    private static final String hentLevendeIdenterIAldersgruppeUrl = "(.*)/hodejegeren/api/v1/levende-identer-i-aldersgruppe/" + avspillergruppeId;
    private static final String arenaForvalterenUrl = "(.*)/arena-forvalteren/api/v1/bruker";

    private SyntetiserArenaRequest syntetiserArenaRequest;
    private NyeBrukereResponse hentBrukereResponse;
    private NyeBrukereResponse sendBrukerResponse;

    @Before
    public void setUp() {
        syntetiserArenaRequest = new SyntetiserArenaRequest(avspillergruppeId, miljoe, 1);

        hentBrukereResponse = new NyeBrukereResponse();
        hentBrukereResponse.setAntallSider(0);
        hentBrukereResponse.setArbeidsoekerList(Collections.emptyList());

        sendBrukerResponse = new NyeBrukereResponse();
        sendBrukerResponse.setAntallSider(0);
        sendBrukerResponse.setArbeidsoekerList(Collections.singletonList(Arbeidsoeker.builder()
                .personident(ident)
                .miljoe(miljoe)
                .build()));
    }

    @Test
    public void shouldRegistrerBrukerIArenaForvalter() throws Exception {

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentLevendeIdenterIAldersgruppeUrl)
                .withResponseBody(Collections.singletonList(ident))
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

        var mvcResultat = mvc.perform(post("/api/v1/syntetisering/generer")
                .queryParam("personident", ident)
                .content(objectMapper.writeValueAsString(syntetiserArenaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentLevendeIdenterIAldersgruppeUrl)
                .withResponseBody(Collections.singletonList(ident))
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaForvalterenUrl)
                .withResponseBody(hentBrukereResponse)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaForvalterenUrl)
                .withResponseBody(sendBrukerResponse)
                .verifyPost();

        NyeBrukereResponse resultat = objectMapper.readValue(mvcResultat,
                new TypeReference<>() {
                });

        assertThat(resultat.getArbeidsoekerList()).hasSize(1);
        assertThat(resultat.getArbeidsoekerList().get(0).getPersonident()).isEqualTo(ident);
        assertThat(resultat.getArbeidsoekerList().get(0).getMiljoe()).isEqualTo(miljoe);
    }

    @Test
    public void shouldRegistrerBrukereIArenaForvalter() throws Exception {

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentLevendeIdenterIAldersgruppeUrl)
                .withResponseBody(Collections.singletonList(ident))
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

        var mvcResultat = mvc.perform(post("/api/v1/syntetisering/generer")
                .content(objectMapper.writeValueAsString(syntetiserArenaRequest))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentLevendeIdenterIAldersgruppeUrl)
                .withResponseBody(Collections.singletonList(ident))
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaForvalterenUrl)
                .withResponseBody(hentBrukereResponse)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaForvalterenUrl)
                .withResponseBody(sendBrukerResponse)
                .verifyPost();

        NyeBrukereResponse resultat = objectMapper.readValue(mvcResultat,
                new TypeReference<>() {
                });

        assertThat(resultat.getArbeidsoekerList()).hasSize(1);
        assertThat(resultat.getArbeidsoekerList().get(0).getPersonident()).isEqualTo(ident);
        assertThat(resultat.getArbeidsoekerList().get(0).getMiljoe()).isEqualTo(miljoe);
    }

    @AfterEach
    public void cleanUp() {
        reset();
    }
}
