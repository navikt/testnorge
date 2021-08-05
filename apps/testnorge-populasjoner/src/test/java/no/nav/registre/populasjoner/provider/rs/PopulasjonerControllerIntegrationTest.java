package no.nav.registre.populasjoner.provider.rs;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.registre.populasjoner.domain.Populasjon;
import no.nav.testnav.libs.testing.JsonWiremockHelper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
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

    private final Populasjon populasjon = Populasjon.MINI_NORGE;

    @Value("${tpsf.avspiller.gruppe}")
    private String avspillergruppe;

    List<String> hodejegerResponse;

    @Before
    public void setUp(){
        hodejegerResponse = new ArrayList<>();

        String ident = "11111164723";
        String ident2 = "11111154723";
        hodejegerResponse.add(ident);
        hodejegerResponse.add(ident2);
    }

    @Test
    public void shouldHentePersonerFraMiniNorge() throws Exception {

        String hentIdenterFraHodejegeren = "(.*)/v1/alle-identer/" + avspillergruppe;

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentIdenterFraHodejegeren)
                .withResponseBody(hodejegerResponse)
                .stubGet();

        var mvcResultat = mockMvc.perform(get("/api/v1/populasjoner/" + populasjon + "/identer")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hentIdenterFraHodejegeren)
                .withResponseBody(hodejegerResponse)
                .verifyGet();

        Set<String> resultat = objectMapper.readValue(mvcResultat, new TypeReference<>() {});

        assertThat(resultat).hasSize(2).containsOnly("11111164723", "11111154723");
    }
}