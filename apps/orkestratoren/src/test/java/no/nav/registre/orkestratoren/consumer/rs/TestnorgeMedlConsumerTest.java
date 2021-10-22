package no.nav.registre.orkestratoren.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;

@ExtendWith(MockitoExtension.class)
@RestClientTest(TestnorgeMedlConsumer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TestnorgeMedlConsumerTest {

    private static final Long AVSPILLERGRUPPE_ID = 123L;
    private static final String MILJOE = "t1";
    @Autowired
    private TestnorgeMedlConsumer testnorgeMedlConsumer;
    @Autowired
    private MockRestServiceServer server;
    @Value("${testnorge-medl.rest.api.url}")
    private String serverUrl;
    private final double prosentfaktor = 0.1;
    private final String expectedBody = "mockBody";

    @Test
    public void shouldStartSyntetisering() {
        var expectedUri = serverUrl + "/v1/syntetisering/generer/";
        stubMedlConsumer(expectedUri);

        var syntetiserMedlRequest = new SyntetiserMedlRequest(AVSPILLERGRUPPE_ID, MILJOE, prosentfaktor);

        var response = testnorgeMedlConsumer.startSyntetisering(syntetiserMedlRequest);

        assertThat(response, equalTo(expectedBody));
    }

    private void stubMedlConsumer(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\":" + AVSPILLERGRUPPE_ID
                        + ",\"miljoe\":\"" + MILJOE + "\""
                        + ",\"prosentfaktor\":" + prosentfaktor + "}"))
                .andRespond(withSuccess("\"" + expectedBody + "\"", MediaType.APPLICATION_JSON));
    }
}