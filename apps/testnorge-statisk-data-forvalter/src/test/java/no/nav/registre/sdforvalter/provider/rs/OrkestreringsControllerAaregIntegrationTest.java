package no.nav.registre.sdforvalter.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;

import no.nav.registre.sdforvalter.consumer.rs.request.aareg.AaregRequest;
import no.nav.registre.sdforvalter.consumer.rs.request.aareg.Arbeidsforhold;
import no.nav.registre.sdforvalter.database.model.AaregModel;
import no.nav.registre.sdforvalter.database.repository.AaregRepository;
import no.nav.registre.sdforvalter.domain.Aareg;
import no.nav.testnav.libs.testing.JsonWiremockHelper;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
@Disabled
public class OrkestreringsControllerAaregIntegrationTest {

    private static final String ENVIRONMENT = "t1";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private AaregRepository aaregRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void shouldInitiateAaregFromDatabase() throws Exception {
        final AaregModel aaregModel = createAaregModel("09876543213", "987654321");
        aaregRepository.save(aaregModel);

        List<AaregRequest> aaregRequestList = Collections.singletonList(
                new AaregRequest(new Arbeidsforhold(new Aareg(aaregModel)), ENVIRONMENT)
        );


        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/syntetisering/sendTilAareg")
                .withRequestBody(aaregRequestList)
                .withResponseBody(Collections.EMPTY_LIST)
                .stubPost();

        mvc.perform(post("/api/v1/orkestrering/aareg/" + ENVIRONMENT)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/syntetisering/sendTilAareg")
                .withRequestBody(aaregRequestList)
                .verifyPost();
    }

    private AaregModel createAaregModel(String fnr, String orgId) {
        AaregModel model = new AaregModel();
        model.setFnr(fnr);
        model.setOrgId(orgId);
        return model;
    }

    @AfterEach
    public void cleanUp() {
        reset();
        aaregRepository.deleteAll();
    }

}