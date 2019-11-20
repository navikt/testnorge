package no.nav.registre.orkestratoren.consumer.rs;

import static org.junit.Assert.assertEquals;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;

@RunWith(SpringRunner.class)
@RestClientTest(TestnorgeTpConsumer.class)
@ActiveProfiles("test")
public class TestnorgeTpConsumerTest {

    @Autowired
    private TestnorgeTpConsumer testnorgeTpConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-tp.rest.api.url}")
    private String serverUrl;

    private Long gruppeId = 10L;
    private String miljoe = "t9";
    private Integer antallPersoner = 3;

    @Test
    public void startSyntetisering() {
        var expectedUri = serverUrl + "/v1/syntetisering/generer/";
        stubTp(expectedUri);

        var entity = testnorgeTpConsumer.startSyntetisering(new SyntetiserTpRequest(gruppeId, miljoe, antallPersoner));
        assertEquals(HttpStatus.OK, entity.getStatusCode());
    }

    private void stubTp(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\": " + gruppeId +
                        ",\"miljoe\":\"" + miljoe + "\"" +
                        ",\"antallPersoner\":" + antallPersoner + "}"))
                .andRespond(withSuccess());
    }
}