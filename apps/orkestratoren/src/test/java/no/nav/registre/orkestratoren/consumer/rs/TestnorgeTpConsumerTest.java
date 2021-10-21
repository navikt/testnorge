package no.nav.registre.orkestratoren.consumer.rs;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;

@ExtendWith(MockitoExtension.class)
@RestClientTest(TestnorgeTpConsumer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TestnorgeTpConsumerTest {

    @Autowired
    private TestnorgeTpConsumer testnorgeTpConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-tp.rest.api.url}")
    private String serverUrl;

    private final Long gruppeId = 10L;
    private final String miljoe = "t9";
    private final Integer antallPersoner = 3;

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