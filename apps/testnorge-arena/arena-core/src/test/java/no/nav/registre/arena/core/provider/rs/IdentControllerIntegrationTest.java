package no.nav.registre.arena.core.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
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

import java.util.Collections;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class IdentControllerIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ident = "01019049900";
    private static final String miljoe = "test";
    private static final String arenaForvalterenUrl = "(.*)/arena-forvalteren/api/v1/bruker";

    private NyeBrukereResponse nyeBrukereResponse;
    private List<Arbeidsoeker> arbeidsoekere;

    @Before
    public void setUp(){
        arbeidsoekere = Collections.singletonList(Arbeidsoeker.builder()
                .personident(ident)
                .miljoe(miljoe)
                .build());

        nyeBrukereResponse = new NyeBrukereResponse();
        nyeBrukereResponse.setAntallSider(1);
        nyeBrukereResponse.setArbeidsoekerList(arbeidsoekere);
    }

    @Test
    public void shouldHenteBrukereFraArenaForvalteren() throws Exception {

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaForvalterenUrl)
                .withResponseBody(nyeBrukereResponse)
                .stubGet();

        var resultat = mvc.perform(get("/api/v1/ident/hent")
                .queryParam("ident", ident)
                .queryParam("miljoe", miljoe)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaForvalterenUrl)
                .withResponseBody(nyeBrukereResponse)
                .verifyGet();

        assertEquals(ident,
                (objectMapper.readValue(resultat,
                        new TypeReference<List<Arbeidsoeker>>() {})).get(0).getPersonident());
        assertEquals(miljoe,
                (objectMapper.readValue(resultat,
                        new TypeReference<List<Arbeidsoeker>>() {})).get(0).getMiljoe());
    }

    @Test
    public void shouldSletteBrukereFraArenaForvalteren() throws Exception {

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaForvalterenUrl)
                .withQueryParam("miljoe", miljoe)
                .withQueryParam("personident", ident)
                .withResponseBody("Resultat")
                .stubDelete();

        var resultat = mvc.perform(delete("/api/v1/ident/slett")
                .queryParam("miljoe", miljoe)
                .content(objectMapper.writeValueAsString(Collections.singletonList(ident)))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaForvalterenUrl)
                .withQueryParam("miljoe", miljoe)
                .withQueryParam("personident", ident)
                .withResponseBody("Resultat")
                .verifyDelete();

        assertEquals(Collections.singletonList(ident),
                objectMapper.readValue(resultat,
                        new TypeReference<List<String>>() {}));
    }

    @AfterEach
    public void tearDown() {
        reset();
    }
}