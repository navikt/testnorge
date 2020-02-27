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
import no.nav.registre.sdForvalter.database.model.TpsModel;
import no.nav.registre.sdForvalter.database.repository.TpsRepository;
import no.nav.registre.sdForvalter.util.JsonTestHelper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class OrkestreringsControllerTpsIntegrationTest {

    @Value("${tps.statisk.avspillergruppeId}")
    private Long staticDataPlaygroup;

    private static final String ENVIRONMENT = "t1";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TpsRepository tpsRepository;

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
    }


    @Test
    public void shouldInitiateTps() throws Exception {
        final TpsModel tps = TpsModel.builder().firstName("Test").lastName("Testen").fnr("01010101011").build();
        tpsRepository.save(tps);

        JsonTestHelper.stubGet(hodejegerenUrlPattern, Collections.EMPTY_SET, objectMapper);
        JsonTestHelper.stubGet(levendeIdenterUrlPattern, Collections.singletonList(tps.getFnr()), objectMapper);
        JsonTestHelper.stubPost(leggTilNyeMeldingerUrlPattern, Collections.EMPTY_LIST, objectMapper);
        JsonTestHelper.stubPost(startAvspillingUrlPattern, new SkdResponse(1, 0, new ArrayList<>(), new ArrayList<>()), objectMapper);

        mvc.perform(post("/api/v1/orkestrering/tps/" + ENVIRONMENT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonTestHelper.verifyPost(startAvspillingUrlPattern);
    }

    @Test
    public void shouldUpdateTpsPlaygroupFromDatabaseAndRunPlaygroup() throws Exception {
        final TpsModel tps = TpsModel.builder().firstName("Test").lastName("Testeb").fnr("01010101011").build();
        tpsRepository.save(tps);

        JsonTestHelper.stubGet(hodejegerenUrlPattern, Collections.EMPTY_SET, objectMapper);
        JsonTestHelper.stubGet(levendeIdenterUrlPattern, Collections.singletonList(tps.getFnr()), objectMapper);
        JsonTestHelper.stubPost(leggTilNyeMeldingerUrlPattern, Collections.EMPTY_LIST, objectMapper);
        JsonTestHelper.stubPost(startAvspillingUrlPattern, new SkdResponse(1, 0, new ArrayList<>(), new ArrayList<>()), objectMapper);

        mvc.perform(post("/api/v1/orkestrering/tps/" + ENVIRONMENT).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonTestHelper.verifyPost(
                leggTilNyeMeldingerUrlPattern,
                new HashSet<>(Collections.singleton(createSkdRequest(tps))),
                objectMapper
        );
        JsonTestHelper.verifyPost(startAvspillingUrlPattern);
    }

    public SkdRequest createSkdRequest(TpsModel tps) {
        return SkdRequest.builder()
                .dateOfBirth(tps.getFnr().substring(0, 6))
                .fnr(tps.getFnr().substring(6))
                .address(tps.getAddress())
                .city(tps.getCity())
                .firstName(tps.getFirstName())
                .lastName(tps.getLastName())
                .postnr(tps.getPostNr())
                .build();
    }


    @After
    public void cleanUp() {
        reset();
        tpsRepository.deleteAll();
    }


}