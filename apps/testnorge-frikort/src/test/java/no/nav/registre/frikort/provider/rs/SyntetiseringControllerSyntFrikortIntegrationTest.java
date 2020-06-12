package no.nav.registre.frikort.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import no.nav.registre.testnorge.test.JsonWiremockHelper;


@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = {"classpath:application-test.properties"}
)
public class SyntetiseringControllerSyntFrikortIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String testIdent = "12345678910";

    @Test
    public void shouldGetResponseFromSyntFrikort() throws Exception {

        Map<String, Integer> request = new HashMap<>();
        request.put(testIdent, 1);

        Map<String, List<SyntFrikortResponse>> response = new HashMap<>();
        response.put(testIdent, Collections.EMPTY_LIST);


        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/api/v1/generate")
                .withRequestBody(request)
                .withResponseBody(response)
                .stubPost();

        mvc.perform(post("/api/v1/syntetisering/generer?leggPaaKoe=false")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("(.*)/api/v1/generate")
                .withRequestBody(request)
                .verifyPost();
    }

    @After
    public void cleanUp() {
        reset();
    }

}
