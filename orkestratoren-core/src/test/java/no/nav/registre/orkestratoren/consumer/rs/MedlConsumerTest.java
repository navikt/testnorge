package no.nav.registre.orkestratoren.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;

@RunWith(SpringRunner.class)
@RestClientTest(MedlConsumer.class)
@ActiveProfiles("test")
public class MedlConsumerTest {

    @Autowired
    private MedlConsumer medlConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-medl.rest-api.url}")
    private String serverUrl;

    private static final Long AVSPILLERGRUPPE_ID = 123L;
    private static final String MILJOE = "t1";
    private double prosentfaktor = 0.1;
    private String expectedBody = "mockBody";

    @Test
    public void shouldStartSyntetisering() {
        String expectedUri = serverUrl + "/v1/syntetisering/generer/";
        stubMedlConsumer(expectedUri);

        SyntetiserMedlRequest syntetiserMedlRequest = new SyntetiserMedlRequest(AVSPILLERGRUPPE_ID, MILJOE, prosentfaktor);

        Object response = medlConsumer.startSyntetisering(syntetiserMedlRequest);

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