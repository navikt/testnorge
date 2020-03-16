package no.nav.registre.sdForvalter.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

import no.nav.registre.sdForvalter.consumer.rs.request.SkdRequest;
import no.nav.registre.sdForvalter.consumer.rs.response.SkdResponse;
import no.nav.registre.sdForvalter.database.model.TpsIdentModel;
import no.nav.registre.sdForvalter.database.repository.TpsIdenterRepository;
import no.nav.registre.sdForvalter.util.JsonTestHelper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class OrkestreringsControllerIdentIntegrationTest {

    @Value("${tps.statisk.avspillergruppeId}")
    private Long staticDataPlaygroup;

    private static final String ENVIRONMENT = "t1";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TpsIdenterRepository tpsIdenterRepository;

    private UrlPathPattern hodejegerenUrlPattern;
    private UrlPathPattern leggTilNyeMeldingerUrlPattern;
    private UrlPathPattern startAvspillingUrlPattern;
    private UrlPathPattern levendeIdenterUrlPattern;

    @Before
    public void setup() {
        hodejegerenUrlPattern = urlPathMatching("(.*)/v1/alle-identer/" + staticDataPlaygroup);
        leggTilNyeMeldingerUrlPattern = urlPathMatching("(.*)/v1/syntetisering/leggTilNyeMeldinger/" + staticDataPlaygroup);
        startAvspillingUrlPattern = urlPathMatching("(.*)/v1/syntetisering/startAvspilling/" + staticDataPlaygroup);
        levendeIdenterUrlPattern = urlPathMatching("(.*)/v1/levende-identer/" + staticDataPlaygroup);
        JsonTestHelper.stubPost(urlPathMatching("(.*)/v1/orkestrering/opprettPersoner/(.*)"));
    }


    @Test
    public void shouldInitiateIdent() throws Exception {
        final TpsIdentModel tpsIdent = create("01010101011", "Test", "Testen");
        tpsIdenterRepository.save(tpsIdent);

        JsonTestHelper.stubGet(hodejegerenUrlPattern, Collections.EMPTY_SET, objectMapper);
        JsonTestHelper.stubGet(levendeIdenterUrlPattern, Collections.singletonList(tpsIdent.getFnr()), objectMapper);
        JsonTestHelper.stubPost(leggTilNyeMeldingerUrlPattern, Collections.EMPTY_LIST, objectMapper);
        JsonTestHelper.stubPost(startAvspillingUrlPattern, new SkdResponse(1, 0, new ArrayList<>(), new ArrayList<>()), objectMapper);

        mvc.perform(post("/api/v1/orkestrering/tps/" + ENVIRONMENT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonTestHelper.verifyPost(startAvspillingUrlPattern);
    }

    @Test
    public void shouldUpdateTpsPlaygroupFromDatabaseAndRunPlaygroup() throws Exception {
        final TpsIdentModel tpsIdent = create("01010101011", "Test", "Testen");
        tpsIdenterRepository.save(tpsIdent);

        JsonTestHelper.stubGet(hodejegerenUrlPattern, Collections.EMPTY_SET, objectMapper);
        JsonTestHelper.stubGet(levendeIdenterUrlPattern, Collections.singletonList(tpsIdent.getFnr()), objectMapper);
        JsonTestHelper.stubPost(leggTilNyeMeldingerUrlPattern, Collections.EMPTY_LIST, objectMapper);
        JsonTestHelper.stubPost(startAvspillingUrlPattern, new SkdResponse(1, 0, new ArrayList<>(), new ArrayList<>()), objectMapper);

        mvc.perform(post("/api/v1/orkestrering/tps/" + ENVIRONMENT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonTestHelper.verifyPost(
                leggTilNyeMeldingerUrlPattern,
                new HashSet<>(Collections.singleton(createSkdRequest(tpsIdent))),
                objectMapper
        );
        JsonTestHelper.verifyPost(startAvspillingUrlPattern);
    }

    public SkdRequest createSkdRequest(TpsIdentModel tpsIdent) {
        return SkdRequest.builder()
                .dateOfBirth(tpsIdent.getFnr().substring(0, 6))
                .fnr(tpsIdent.getFnr())
                .address(tpsIdent.getAddress())
                .city(tpsIdent.getCity())
                .firstName(tpsIdent.getFirstName())
                .lastName(tpsIdent.getLastName())
                .postnr(tpsIdent.getPostNr())
                .build();
    }

    private TpsIdentModel create(String fnr, String firstName, String lastName) {
        TpsIdentModel model = new TpsIdentModel();
        model.setFnr(fnr);
        model.setFirstName(firstName);
        model.setLastName(lastName);
        return model;
    }

    @After
    public void cleanUp() {
        reset();
        tpsIdenterRepository.deleteAll();
    }


}