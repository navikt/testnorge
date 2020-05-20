package no.nav.registre.ereg.provider.rs;


import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import no.nav.registre.ereg.config.TestConfig;
import no.nav.registre.ereg.provider.rs.request.EregDataRequest;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class OrganisasjonControllerIntegrationTest {

    private static final String WINDOWS_LINE_SEPERATOR = "\r\n";
    private static final String UNIX_LINE_SEPERATOR = "\n";

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private MockMvc mvc;

    @Test
    public void should_change_name_for_BEDR() throws Exception {
        assertRequestCorrespondingToResponse("change_BEDR_navn_request.txt", "change_BEDR_navn_response.json");
    }

    @Test
    public void should_create_new_AS_with_BEDR() throws Exception {
        assertRequestCorrespondingToResponse("new_AS_with_BEDRs_request.txt", "new_AS_with_BEDRs_response.json");
    }

    @Test
    public void should_create_new_BEDR() throws Exception {
        assertRequestCorrespondingToResponse("new_BEDR_request.txt", "new_BEDR_response.json");
    }

    @Test
    public void should_create_new_KOMN_with_ORGL_with_BEDR() throws Exception {
        assertRequestCorrespondingToResponse("new_KOMN_with_ORGL_with_BEDR_request.txt", "new_KOMN_with_ORGL_with_BEDR_response.json");
    }

    @Test
    public void should_create_new_BEDR_with_FADR() throws Exception {
        assertRequestCorrespondingToResponse("new_BEDR_with_FADR_request.txt", "new_BEDR_with_FADR_response.json");
    }

    @Test
    public void should_create_new_BEDR_with_EPOS() throws Exception {
        assertRequestCorrespondingToResponse("new_BEDR_with_EPOS_request.txt", "new_BEDR_with_EPOS_response.json");

    }

    @Test
    public void should_not_create_from_empty_list() throws Exception {
        assertRequestCorrespondingToResponse("empty_request.txt", "empty_response.json");
    }

    private List<EregDataRequest> getListFromJson(String json) throws IOException {
        return mapper.readValue(json, new TypeReference<List<EregDataRequest>>() {
        });
    }


    public void assertRequestCorrespondingToResponse(String request, String response) throws Exception {
        String body = loadStaticResourceAsString(request);
        MockHttpServletRequestBuilder postOrganisasjoner = post("/api/v1/organisasjoner").content(body);
        MockHttpServletResponse actual = mvc.perform(postOrganisasjoner).andExpect(status().isOk()).andReturn().getResponse();
        List<EregDataRequest> expected = getListFromJson(loadStaticResourceAsString(response));
        assertThat(getListFromJson(actual.getContentAsString())).isEqualTo(expected);
    }

    private String loadStaticResourceAsString(String fileName) throws IOException {
        return FileCopyUtils
                .copyToString(new InputStreamReader(this.getClass().getResourceAsStream("/static/organisasjon/" + fileName)))
                .replaceAll(WINDOWS_LINE_SEPERATOR, UNIX_LINE_SEPERATOR);
    }
}