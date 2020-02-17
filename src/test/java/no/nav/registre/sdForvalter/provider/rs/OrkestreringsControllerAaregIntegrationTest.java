package no.nav.registre.sdForvalter.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import org.junit.After;
import org.junit.Test;
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
import java.util.List;

import no.nav.registre.sdForvalter.consumer.rs.request.aareg.AaregRequest;
import no.nav.registre.sdForvalter.consumer.rs.request.aareg.Arbeidsforhold;
import no.nav.registre.sdForvalter.database.model.AaregModel;
import no.nav.registre.sdForvalter.database.repository.AaregRepository;
import no.nav.registre.sdForvalter.util.JsonTestHelper;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class OrkestreringsControllerAaregIntegrationTest {

    public static final String ENVIRONMENT = "t1";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AaregRepository aaregRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldInitiateAaregFromDatabase() throws Exception {
        final AaregModel aaregModel = createAaregModel("09876543213", 12345678903L);
        final UrlPathPattern sendTilAaregUrlPattern = urlPathMatching("(.*)/v1/syntetisering/sendTilAareg");
        aaregRepository.save(aaregModel);

        List<AaregRequest> aaregRequestList = Collections.singletonList(
                new AaregRequest(new Arbeidsforhold(aaregModel), ENVIRONMENT)
        );

        JsonTestHelper.stubPost(sendTilAaregUrlPattern, aaregRequestList, Collections.EMPTY_LIST, objectMapper);
        mvc.perform(post("/api/v1/orkestrering/aareg/" + ENVIRONMENT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        JsonTestHelper.verifyPost(sendTilAaregUrlPattern, aaregRequestList, objectMapper);
    }

    private AaregModel createAaregModel(String fnr, long orgId) {
        return AaregModel.builder().fnr(fnr).orgId(orgId).build();
    }

    @After
    public void cleanUp() {
        reset();
        aaregRepository.deleteAll();
    }

}