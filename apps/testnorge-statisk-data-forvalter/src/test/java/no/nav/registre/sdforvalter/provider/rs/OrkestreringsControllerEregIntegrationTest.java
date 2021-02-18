package no.nav.registre.sdforvalter.provider.rs;


import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import org.junit.After;
import org.junit.Ignore;
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

import java.util.ArrayList;
import java.util.Collections;

import no.nav.registre.sdforvalter.consumer.rs.request.ereg.EregMapperRequest;
import no.nav.registre.sdforvalter.database.model.EregModel;
import no.nav.registre.sdforvalter.database.repository.EregRepository;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.testnorge.libs.test.JsonWiremockHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class OrkestreringsControllerEregIntegrationTest {

    private static final String ENVIRONMENT = "t1";
    private static final UrlPathPattern CREATE_IN_EREG_URL_PATTERN = urlPathMatching("(.*)/v1/orkestrering/opprett");

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private EregRepository eregRepository;

    @Test
    @Ignore
    public void shouldInitializeEregFromDatabase() throws Exception {
        EregModel model = createEregModel("12345678903", "BEDF");
        eregRepository.save(model);

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/orkestrering/opprett")
                .stubPost();


        mvc.perform(post("/api/v1/orkestrering/ereg/" + ENVIRONMENT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());


        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/v1/orkestrering/opprett")
                .withRequestBody(Collections.singletonList(new EregMapperRequest(new Ereg(model, new ArrayList<>()), false)))
                .stubPost();
    }

    private EregModel createEregModel(String orgId, String enhetstype) {
        return createEregModel(orgId, enhetstype, null);
    }

    private EregModel createEregModel(String orgId, String enhetstype, EregModel parent) {
        EregModel model = new EregModel();
        model.setOrgnr(orgId);
        model.setEnhetstype(enhetstype);
        model.setParent(parent);
        return model;
    }

    @After
    public void cleanUp() {
        reset();
        eregRepository.deleteAll();
    }
}