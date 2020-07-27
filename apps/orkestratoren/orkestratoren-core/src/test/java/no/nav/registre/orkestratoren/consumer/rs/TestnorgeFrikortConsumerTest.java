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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

import no.nav.registre.orkestratoren.consumer.rs.response.GenererFrikortResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.GenererFrikortResponse.LeggPaaKoeStatus;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserFrikortRequest;

@RunWith(SpringRunner.class)
@RestClientTest(TestnorgeFrikortConsumer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TestnorgeFrikortConsumerTest {

    @Autowired
    private TestnorgeFrikortConsumer testnorgeFrikortConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-frikort.rest.api.url}")
    private String serverUrl;

    private static final Long AVSPILLERGRUPPE_ID = 123L;
    private static final String MILJOE = "t1";
    private SyntetiserFrikortRequest syntetiserFrikortRequest;
    private int antallNyeIdenter = 2;
    private String xml1 = "firstXml";
    private String xml2 = "secondXml";

    @Before
    public void setUp() {
        syntetiserFrikortRequest = new SyntetiserFrikortRequest(AVSPILLERGRUPPE_ID, MILJOE, antallNyeIdenter);
    }

    @Test
    public void shouldStartSyntetisering() {
        var expectedUri = serverUrl + "/v1/syntetisering/generer?leggPaaKoe=true";
        stubFrikortSyntConsumer(expectedUri);

        var response = testnorgeFrikortConsumer.startSyntetisering(syntetiserFrikortRequest);

        assertThat(response.size(), equalTo(antallNyeIdenter));
        assertThat(response.get(0).getXml(), equalTo(xml1));
        assertThat(response.get(0).getLagtPaaKoe(), equalTo(LeggPaaKoeStatus.OK));
        assertThat(response.get(1).getXml(), equalTo(xml2));
        assertThat(response.get(1).getLagtPaaKoe(), equalTo(LeggPaaKoeStatus.ERROR));
    }

    private void stubFrikortSyntConsumer(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\":" + AVSPILLERGRUPPE_ID
                        + ",\"miljoe\":\"" + MILJOE + "\""
                        + ",\"antallNyeIdenter\":" + antallNyeIdenter + "}"))
                .andRespond(withSuccess("["
                        + "{"
                        + "\"lagtPaaKoe\":\"OK\","
                        + "\"xml\":\"" + xml1 + "\"},"
                        + "{"
                        + "\"lagtPaaKoe\":\"ERROR\","
                        + "\"xml\":\"" + xml2 + "\""
                        + "}"
                        + "]", MediaType.APPLICATION_JSON));
    }
}