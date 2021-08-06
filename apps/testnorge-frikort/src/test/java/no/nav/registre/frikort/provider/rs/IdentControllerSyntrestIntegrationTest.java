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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import no.nav.registre.frikort.provider.rs.request.IdentMedAntallFrikort;
import no.nav.registre.frikort.provider.rs.request.IdentRequest;
import no.nav.registre.testnorge.consumers.hodejegeren.response.PersondataResponse;
import no.nav.testnav.libs.testing.JsonWiremockHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = { "classpath:application-test.properties" }
)
public class IdentControllerSyntrestIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String testIdent = "12345678910";
    private final String samhandlerIdent = "01010101010";

    @Test
    public void shouldGetResponseFromSyntrest() throws Exception {
        var request = IdentRequest.builder()
                .identer(Collections.singletonList(IdentMedAntallFrikort.builder()
                        .ident(testIdent)
                        .antallFrikort(1)
                        .build()))
                .build();

        Map<String, Integer> syntRequest = new HashMap<>();
        syntRequest.put(testIdent, 1);

        Map<String, List<SyntFrikortResponse>> syntResponse = new HashMap<>();
        syntResponse.put(testIdent, Collections.EMPTY_LIST);

        List<String> hodejegerenGetIdenterResponse = new ArrayList<>();
        hodejegerenGetIdenterResponse.add(samhandlerIdent);

        PersondataResponse hodejegerenGetPersondataResponse = PersondataResponse.builder().build();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/testnorge-hodejegeren/api/v1/alle-identer/100001163")
                .withResponseBody(hodejegerenGetIdenterResponse)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/testnorge-hodejegeren/api/v1/persondata")
                .withQueryParam("miljoe", "q2")
                .withQueryParam("ident", samhandlerIdent)
                .withResponseBody(hodejegerenGetPersondataResponse)
                .stubGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/syntrest/api/v1/generate/frikort")
                .withRequestBody(syntRequest)
                .withResponseBody(syntResponse)
                .stubPost();

        mvc.perform(post("/api/v1/ident/syntetisk?leggPaaKoe=false")
                .content(objectMapper.writeValueAsString(request))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/testnorge-hodejegeren/api/v1/alle-identer/100001163")
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/testnorge-hodejegeren/api/v1/persondata")
                .withQueryParam("miljoe", "q2")
                .withQueryParam("ident", samhandlerIdent)
                .verifyGet();

        JsonWiremockHelper
                .builder(objectMapper)
                .withUrlPathMatching("/syntrest/api/v1/generate/frikort")
                .withRequestBody(syntRequest)
                .verifyPost();
    }

    @After
    public void cleanUp() {
        reset();
    }

}
