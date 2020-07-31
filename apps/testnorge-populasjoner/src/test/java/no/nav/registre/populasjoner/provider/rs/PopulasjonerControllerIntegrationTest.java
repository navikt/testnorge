package no.nav.registre.populasjoner.provider.rs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.brukere.Arbeidsoeker;
import no.nav.registre.testnorge.domain.dto.arena.testnorge.vedtak.NyeBrukereResponse;
import no.nav.registre.testnorge.test.JsonWiremockHelper;
import org.junit.Before;
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
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class PopulasjonerControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private static final String ident = "11111164723";
    private static final String populasjon = "TENOR";
    private static final String arenaForvalterenUrl = "(.*)/arena-forvalteren/api/v1/bruker";
    private static final String miljoe = "test";

    private NyeBrukereResponse nyeBrukereResponse;
    private List<Arbeidsoeker> arbeidsoekerList;

    @Before
    public void setUp(){
        arbeidsoekerList = Collections.singletonList(Arbeidsoeker.builder()
                .personident(ident)
                .miljoe(miljoe)
                .build());

        nyeBrukereResponse = new NyeBrukereResponse();
        nyeBrukereResponse.setAntallSider(1);
        nyeBrukereResponse.setArbeidsoekerList(arbeidsoekerList);

    }

    @Test
    public void shouldHentePersonerFraArena() throws Exception {

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaForvalterenUrl)
                .withResponseBody(arbeidsoekerList)
                .stubGet();

        var mvcResultat = mockMvc.perform(get("/api/v1/populasjoner/" + populasjon + "/identer")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(arenaForvalterenUrl)
                .withResponseBody(arbeidsoekerList)
                .verifyGet();

        Set<String> resultat = objectMapper.readValue(mvcResultat, new TypeReference<>() {});

        assertThat(resultat).hasSize(1);
    }
}