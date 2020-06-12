package no.nav.registre.ereg.provider.rs;

import static com.github.tomakehurst.wiremock.client.WireMock.findAll;
import static com.github.tomakehurst.wiremock.client.WireMock.postRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.reset;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.verification.LoggedRequest;
import org.junit.After;
import org.junit.Before;
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
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.ereg.config.TestConfig;
import no.nav.registre.ereg.consumer.rs.request.JenkinsCrumbRequest;
import no.nav.registre.testnorge.test.JsonWiremockHelper;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TestConfig.class)
@AutoConfigureWireMock(port = 0)
@AutoConfigureMockMvc
@TestPropertySource(
        locations = "classpath:application-test.properties"
)
public class OrkestreringControllerIntegrationTest {

    private final static String BEREG007_PATTERN = "(.*)/view/Registre/job/Start_BEREG007/buildWithParameters";
    private static final String WINDOWS_LINE_SEPERATOR = "\r\n";
    private static final String UNIX_LINE_SEPERATOR = "\n";

    @Autowired
    private MockMvc mvc;

    private ObjectMapper mapper = new ObjectMapper();

    @Before
    public void setup() throws Exception {
        JenkinsCrumbRequest crumbRequest = JenkinsCrumbRequest.builder().crumb("test").build();

        JsonWiremockHelper.builder(mapper)
                .withUrlPathMatching("(.*)/crumbIssuer/api/json")
                .withResponseBody(crumbRequest)
                .stubGet();

        JsonWiremockHelper.builder(mapper)
                .withUrlPathMatching(BEREG007_PATTERN)
                .withResponseBody("")
                .stubPost();
    }


    @Test
    public void endre_enkelt_BEDER_navn() throws Exception {
        testOpprett("change_BEDR_navn_request.json", "change_BEDR_navn_response.txt");
    }

    @Test
    public void opprett_enklt_ny_BEDR() throws Exception {
        testOpprett("new_BEDR_request.json", "new_BEDR_response.txt");
    }

    @Test
    public void opprett_enklt_ny_BEDR_with_lowercase() throws Exception {
        testOpprett("new_BEDR_lowercase_request.json", "new_BEDR_lowercase_response.txt");
    }

    @Test
    public void opprett_ny_BEDRer_med_ny_AS_som_juridisk_enhet() throws Exception {
        testOpprett("new_AS_with_BEDRs_request.json", "new_AS_with_BEDRs_response.txt");
    }

    @Test
    public void ikke_opprett_naa_request_er_en_tom_liste() throws Exception {

        mvc.perform(post("/api/v1/orkestrering/opprett?lastOpp=true&miljoe=q2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loadStaticResourceAsString("empty_request.json"))
        ).andExpect(status().isNoContent());

        List<LoggedRequest> requests = findAll(postRequestedFor(urlPathMatching(BEREG007_PATTERN)));
        assertThat(requests).hasSize(0);
    }

    @Test
    public void opprett_KOMN_med_ORGL_med_BEDER() throws Exception {
        testOpprett("new_KOMN_with_ORGL_with_BEDR_request.json", "new_KOMN_with_ORGL_with_BEDR_response.txt");
    }


    @After
    public void after() {
        reset();
    }

    private void testOpprett(String request, String response) throws Exception {
        mvc.perform(post("/api/v1/orkestrering/opprett?lastOpp=true&miljoe=q2")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loadStaticResourceAsString(request))
        ).andExpect(status().isNoContent());

        List<LoggedRequest> requests = findAll(postRequestedFor(urlPathMatching(BEREG007_PATTERN)));
        assertThat(requests).hasSize(1);
        String actual = trimLines(new String(requests.get(0).getBody(), StandardCharsets.ISO_8859_1));
        String expected = loadStaticResourceAsString(response);
        assertThat(actual).contains(expected);
    }

    private String trimLines(String value) {
        return Arrays.stream(value.split(UNIX_LINE_SEPERATOR))
                .map(String::trim).collect(Collectors.joining(UNIX_LINE_SEPERATOR));
    }

    private String localDateAsString(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        return date.format(formatter);
    }


    private String loadStaticResourceAsString(String fileName) throws IOException {
        return FileCopyUtils
                .copyToString(new InputStreamReader(this.getClass().getResourceAsStream("/static/" + fileName)))
                .replaceAll(WINDOWS_LINE_SEPERATOR, UNIX_LINE_SEPERATOR)
                .replaceAll("\\{date\\}", localDateAsString(LocalDate.now()));
    }
}