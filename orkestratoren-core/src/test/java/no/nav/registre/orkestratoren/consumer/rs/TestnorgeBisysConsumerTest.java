package no.nav.registre.orkestratoren.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;

@RunWith(SpringRunner.class)
@RestClientTest(TestnorgeBisysConsumer.class)
@ActiveProfiles("test")
public class TestnorgeBisysConsumerTest {

    @Autowired
    private TestnorgeBisysConsumer testnorgeBisysConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-bisys.rest.api.url}")
    private String serverUrl;

    private static final Long AVSPILLERGRUPPE_ID = 123L;
    private static final String MILJOE = "t1";
    private List<String> fnrs;
    private SyntetiserBisysRequest syntetiserBisysRequest;

    @Before
    public void setUp() {
        fnrs = new ArrayList<>(Arrays.asList("01010101010", "02020202020"));
        syntetiserBisysRequest = new SyntetiserBisysRequest(AVSPILLERGRUPPE_ID, MILJOE, fnrs.size());
    }

    @Test
    public void shouldStartSyntetisering() {
        var expectedUri = serverUrl + "/v1/syntetisering/generer";
        stubBisysSyntConsumer(expectedUri);

        var response = (ResponseEntity) testnorgeBisysConsumer.startSyntetisering(syntetiserBisysRequest);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    private void stubBisysSyntConsumer(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\":" + AVSPILLERGRUPPE_ID
                        + ",\"miljoe\":\"" + MILJOE + "\""
                        + ",\"antallNyeIdenter\":" + fnrs.size() + "}"))
                .andRespond(withSuccess());
    }
}