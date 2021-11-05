package no.nav.dolly.consumer.jira;

import com.fasterxml.jackson.databind.ObjectMapper;
import no.nav.dolly.domain.jira.Project;
import no.nav.dolly.properties.JiraProps;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
@ExtendWith(SpringExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application.yaml")
@AutoConfigureWireMock(port = 0)
public class JiraConsumerTest {

    private static final String HOST = "jira/api/v1";
    private static final String AUX_HEADER = "header";
    private static final String AUX_HEADER_CONTENTS = "aux";
    private static final String METADATA = "/createmeta";

    @MockBean
    private JwtDecoder jwtDecoder;

    @Mock
    private HttpEntity httpEntity;

    @Mock
    private ObjectMapper objectMapper;

    @Autowired
    private JiraProps jiraProps;

    @Autowired
    private JiraConsumer jiraConsumer;

    @BeforeEach
    public void setup() {
        when(httpEntity.getHeaders()).thenReturn(HttpHeaders.EMPTY);
    }

    @Test
    public void excuteRequest() {

        stubGetJira();

        ResponseEntity<Project> response = jiraConsumer.excuteRequest("api/v1/test", HttpMethod.GET, httpEntity, Project.class);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void excuteMetadataRequest() {

        MultiValueMap<String, String> queries = new LinkedMultiValueMap<>();

        queries.add("projectKeys", "testy");
        queries.add("issuetypeIds", "1234");
        queries.add("expand", "projects.issuetypes.fields");

        stubGetMetadataJira();

        ResponseEntity<Project> response = jiraConsumer.getOpenAmMetadata(format("%s%s", "api/v1/test", METADATA),
                httpEntity,
                queries);

        assertThat(response.getStatusCode(), is(HttpStatus.OK));
    }

    @Test
    public void createHttpHeaders() {
        HttpHeaders httpHeaders = jiraConsumer.createHttpHeaders(MediaType.APPLICATION_JSON);

        assertThat(httpHeaders.size(), is(equalTo(3)));
        assertThat(httpHeaders.get("X-Atlassian-Token"), containsInAnyOrder("no-check"));
        assertThat(httpHeaders.get(HttpHeaders.CONTENT_TYPE), containsInAnyOrder(MediaType.APPLICATION_JSON_VALUE));
        assertThat(httpHeaders.get(HttpHeaders.AUTHORIZATION), containsInAnyOrder("Basic ZHVtbXk6ZHVtbXk="));
    }

    @Test
    public void createHttpHeadersExtraHeader() {
        HttpHeaders inputHeaders = new HttpHeaders();
        inputHeaders.add(AUX_HEADER, AUX_HEADER_CONTENTS);
        HttpHeaders httpHeaders = jiraConsumer.createHttpHeaders(MediaType.MULTIPART_FORM_DATA, inputHeaders);

        assertThat(httpHeaders.get(HttpHeaders.CONTENT_TYPE), containsInAnyOrder(MediaType.MULTIPART_FORM_DATA_VALUE));
        assertThat(httpHeaders.get(AUX_HEADER), containsInAnyOrder(AUX_HEADER_CONTENTS));
    }

    private void stubGetJira() {

        stubFor(get(urlPathMatching("(.*)/jira/api/v1/test"))
                .willReturn(ok()
                        .withBody("{}")
                        .withHeader("Content-Type", "application/json")));
    }

    private void stubGetMetadataJira() {

        stubFor(get(urlPathMatching("(.*)/jira/api/v1/test/createmeta"))
                .withQueryParam("projectKeys", matching("testy"))
                .withQueryParam("issuetypeIds", matching("1234"))
                .withQueryParam("expand", matching("projects.issuetypes.fields"))
                .willReturn(ok()
                        .withBody("{}")
                        .withHeader("Content-Type", "application/json")));
    }
}