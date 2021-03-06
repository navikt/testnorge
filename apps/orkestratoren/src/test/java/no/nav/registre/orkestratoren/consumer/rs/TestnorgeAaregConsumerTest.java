package no.nav.registre.orkestratoren.consumer.rs;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;
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
@RestClientTest(TestnorgeAaregConsumer.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@ActiveProfiles("test")
public class TestnorgeAaregConsumerTest {

    @Autowired
    private TestnorgeAaregConsumer testnorgeAaregConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge-aareg.rest.api.url}")
    private String serverUrl;

    private static final Long AVSPILLERGRUPPE_ID = 123L;
    private static final String MILJOE = "t1";
    private List<String> fnrs;
    private SyntetiserAaregRequest syntetiserAaregRequest;
    private String fnr1 = "01010101010";
    private String fnr2 = "02020202020";
    private boolean sendAlleEksisterende = false;

    @Before
    public void setUp() {
        fnrs = new ArrayList<>();
        fnrs.addAll(Arrays.asList(fnr1, fnr2));
        syntetiserAaregRequest = new SyntetiserAaregRequest(AVSPILLERGRUPPE_ID, MILJOE, fnrs.size());
    }

    @Test
    public void shouldStartSyntetisering() {
        var expectedUri = serverUrl + "/v1/syntetisering/generer?sendAlleEksisterende={sendAlleEksisterende}";
        stubAaregSyntConsumer(expectedUri);

        var response = testnorgeAaregConsumer.startSyntetisering(syntetiserAaregRequest, sendAlleEksisterende);

        assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
    }

    private void stubAaregSyntConsumer(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri, sendAlleEksisterende))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"avspillergruppeId\":" + AVSPILLERGRUPPE_ID
                        + ",\"miljoe\":\"" + MILJOE + "\""
                        + ",\"antallNyeIdenter\":" + fnrs.size() + "}"))
                .andRespond(withSuccess());
    }
}