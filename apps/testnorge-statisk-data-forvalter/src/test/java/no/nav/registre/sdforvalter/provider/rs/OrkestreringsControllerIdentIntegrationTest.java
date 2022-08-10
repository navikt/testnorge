package no.nav.registre.sdforvalter.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;

import no.nav.registre.sdforvalter.consumer.rs.response.SkdResponse;
import no.nav.registre.sdforvalter.database.model.TpsIdentModel;
import no.nav.registre.sdforvalter.database.repository.TpsIdenterRepository;
import no.nav.testnav.libs.testing.JsonWiremockHelper;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.yml"
)
@Disabled
class OrkestreringsControllerIdentIntegrationTest {

    @Value("${tps.statisk.avspillergruppeId}")
    private Long staticDataPlaygroup;

    private static final String ENVIRONMENT = "t1";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TpsIdenterRepository tpsIdenterRepository;

    private String hodejegerenUrlPattern;
    private String startAvspillingUrlPattern;
    private String levendeIdenterUrlPattern;

    @BeforeEach
    public void setup() {
        hodejegerenUrlPattern = "(.*)/v1/alle-identer/" + staticDataPlaygroup;
        startAvspillingUrlPattern = "(.*)/v1/startAvspilling/" + staticDataPlaygroup;
        levendeIdenterUrlPattern = "(.*)/v1/levende-identer/" + staticDataPlaygroup;

        JsonWiremockHelper.builder(objectMapper).withUrlPathMatching("(.*)/v1/orkestrering/opprettPersoner/(.*)").stubPost();
    }


    @Test
    void shouldInitiateIdent() throws Exception {
        final TpsIdentModel tpsIdent = create("01010101011", "Test", "Testen");
        tpsIdenterRepository.save(tpsIdent);

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hodejegerenUrlPattern)
                .withResponseBody(Collections.EMPTY_SET)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(levendeIdenterUrlPattern)
                .withResponseBody(Collections.singletonList(tpsIdent.getFnr()))
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(startAvspillingUrlPattern)
                .withResponseBody(new SkdResponse(1, 0, new ArrayList<>(), new ArrayList<>()))
                .stubPost();

        mvc.perform(post("/api/v1/orkestrering/tps/" + ENVIRONMENT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(startAvspillingUrlPattern)
                .verifyPost();
    }

    @Test
    void shouldUpdateTpsPlaygroupFromDatabaseAndRunPlaygroup() throws Exception {
        final TpsIdentModel tpsIdent = create("01010101011", "Test", "Testen");
        tpsIdenterRepository.save(tpsIdent);


        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(hodejegerenUrlPattern)
                .withResponseBody(Collections.EMPTY_SET)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(levendeIdenterUrlPattern)
                .withResponseBody(Collections.singletonList(tpsIdent.getFnr()))
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(startAvspillingUrlPattern)
                .withResponseBody(new SkdResponse(1, 0, new ArrayList<>(), new ArrayList<>()))
                .stubPost();


        mvc.perform(post("/api/v1/orkestrering/tps/" + ENVIRONMENT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching(startAvspillingUrlPattern)
                .verifyPost();
    }

    private TpsIdentModel create(String fnr, String firstName, String lastName) {
        TpsIdentModel model = new TpsIdentModel();
        model.setFnr(fnr);
        model.setFirstName(firstName);
        model.setLastName(lastName);
        return model;
    }

    @AfterEach
    public void cleanUp() {
        reset();
        tpsIdenterRepository.deleteAll();
    }


}