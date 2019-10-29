package no.nav.dolly.consumer.aareg;

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

@RunWith(SpringRunner.class)
@RestClientTest(TestnorgeAaregConsumer.class)
@ActiveProfiles("test")
public class TestnorgeAaregConsumerTest {

    @Autowired
    private TestnorgeAaregConsumer testnorgeAaregConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${providers.aaregdata.url}")
    private String serverUrl;

    private String ident = "01010101010";
    private String miljoe = "t0";

    @Test
    public void hentArbeidsforhold() {
        String expectedUri = serverUrl + "/v1/arbeidsforhold?ident={ident}&miljoe={miljoe}";
        stubHentArbeidsforhold(expectedUri);

        testnorgeAaregConsumer.hentArbeidsforhold(ident, miljoe);
    }

    private void stubHentArbeidsforhold(String expectedUri) {
        server.expect(requestToUriTemplate(expectedUri, ident, miljoe))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("[{}]", MediaType.APPLICATION_JSON));
    }
}