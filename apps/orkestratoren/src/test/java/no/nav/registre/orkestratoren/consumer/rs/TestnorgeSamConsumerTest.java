package no.nav.registre.orkestratoren.consumer.rs;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RunWith(SpringRunner.class)
@RestClientTest(TestnorgeSamConsumer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TestnorgeSamConsumerTest {

    @Autowired
    private TestnorgeSamConsumer testnorgeSamConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-sam.rest.api.url}")
    private String serverUrl;

    private static final Long AVSPILLERGRUPPE_ID = 123L;
    private static final String MILJOE = "t1";
    private List<String> fnrs;
    private SyntetiserSamRequest syntetiserSamRequest;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";

    @Before
    public void setUp() {
        fnrs = new ArrayList<>();
        fnrs.addAll(Arrays.asList(fnr1, fnr2));
        syntetiserSamRequest = new SyntetiserSamRequest(AVSPILLERGRUPPE_ID, MILJOE, fnrs.size());
    }

    @Test
    public void shouldStartSyntetisering() {
        var expectedUri = serverUrl + "/v1/syntetisering/generer";
        stubSamSyntConsumer(expectedUri);

        var response = testnorgeSamConsumer.startSyntetisering(syntetiserSamRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    private void stubSamSyntConsumer(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\":" + AVSPILLERGRUPPE_ID
                        + ",\"miljoe\":\"" + MILJOE + "\""
                        + ",\"antallMeldinger\":" + fnrs.size() + "}"))
                .andRespond(withSuccess());
    }
}