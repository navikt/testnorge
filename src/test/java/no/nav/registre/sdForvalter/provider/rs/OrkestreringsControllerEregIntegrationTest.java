package no.nav.registre.sdForvalter.provider.rs;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;
import no.nav.registre.sdForvalter.consumer.rs.request.ereg.EregMapperRequest;
import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.database.repository.EregRepository;
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

import java.util.Arrays;
import java.util.Collections;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static no.nav.registre.sdForvalter.util.JsonTestHelper.stubPost;
import static no.nav.registre.sdForvalter.util.JsonTestHelper.verifyPost;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
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
    public void shouldInitializeEregFromDatabase() throws Exception {
        EregModel model = createEregModel("12345678903", "BEDF");
        eregRepository.save(model);

        stubPost(CREATE_IN_EREG_URL_PATTERN);
        mvc.perform(post("/api/v1/orkestrering/ereg/" + ENVIRONMENT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verifyPost(
                CREATE_IN_EREG_URL_PATTERN,
                Collections.singletonList(new EregMapperRequest(model)),
                objectMapper
        );
    }

    @Test
    public void shouldInitializeEregWithAndSplitOnParents() throws Exception {
        EregModel firstGeneration = createEregModel("12345678903", "AAS");
        EregModel secondGeneration01 = createEregModel("12345678904", "AS", firstGeneration);
        EregModel secondGeneration02 = createEregModel("12345678905", "BEDF", firstGeneration);
        EregModel thirdGeneration = createEregModel("12345678906", "BEDF", secondGeneration01);

        eregRepository.saveAll(Arrays.asList(firstGeneration, secondGeneration01, secondGeneration02, thirdGeneration));

        stubPost(CREATE_IN_EREG_URL_PATTERN);
        mvc.perform(post("/api/v1/orkestrering/ereg/" + ENVIRONMENT)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verifyPost(
                CREATE_IN_EREG_URL_PATTERN,
                Collections.singletonList(new EregMapperRequest(firstGeneration)),
                objectMapper
        );

        verifyPost(
                CREATE_IN_EREG_URL_PATTERN,
                Arrays.asList(
                        new EregMapperRequest(secondGeneration01),
                        new EregMapperRequest(secondGeneration02)
                ),
                objectMapper
        );

        verifyPost(
                CREATE_IN_EREG_URL_PATTERN,
                Collections.singletonList(new EregMapperRequest(thirdGeneration)),
                objectMapper
        );
    }

    private EregModel createEregModel(String orgId, String enhetstype) {
        return createEregModel(orgId, enhetstype, null);
    }


    private EregModel createEregModel(String orgId, String enhetstype, EregModel parent) {
        return EregModel.builder().orgnr(orgId).enhetstype(enhetstype).parent(parent).build();
    }

    @After
    public void cleanUp() {
        reset();
        eregRepository.deleteAll();
    }


}
