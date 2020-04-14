package no.nav.registre.testnorge.elsam.consumer.rs;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
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

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import no.nav.registre.testnorge.elsam.consumer.rs.response.tss.Response110;
import no.nav.registre.testnorge.elsam.consumer.rs.response.tss.Response111;
import no.nav.registre.testnorge.elsam.consumer.rs.response.tss.TssResponse;

@RunWith(SpringRunner.class)
@RestClientTest(TSSConsumer.class)
@ActiveProfiles("test")
public class TSSConsumerTest {

    @Autowired
    private TSSConsumer tssConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${testnorge.rest-api.tss}")
    private String serverUrl;

    private String fnrLege1 = "19098723229";
    private String fnrLege2 = "03126126084";
    private Long avspillergruppeId = 123L;
    private String miljoe = "t1";

    @Test
    public void shouldHenteLeger() throws IOException {
        String expectedUri = serverUrl + "/v1/ident/samhandlere/{avspillergruppeId}?miljoe={miljoe}";
        stubTssConsumer(expectedUri);

        Map<String, TssResponse> response = tssConsumer.hentLeger(avspillergruppeId, miljoe).getBody();

        assert response != null;
        ObjectMapper objectMapper = new ObjectMapper();
        Response110 fnr1Response110 = objectMapper.convertValue(response.get(fnrLege1).getResponse110().get(0), Response110.class);
        Response111 fnr1Response111First = objectMapper.convertValue(response.get(fnrLege1).getResponse111().get(0), Response111.class);
        Response111 fnr1Response111Second = objectMapper.convertValue(response.get(fnrLege1).getResponse111().get(1), Response111.class);

        Response110 fnr2Response110 = objectMapper.convertValue(response.get(fnrLege2).getResponse110().get(0), Response110.class);
        Response111 fnr2Response111First = objectMapper.convertValue(response.get(fnrLege2).getResponse111().get(0), Response111.class);
        Response111 fnr2Response111Second = objectMapper.convertValue(response.get(fnrLege2).getResponse111().get(1), Response111.class);

        assertThat(fnr1Response110.getNavn(), equalTo("PRODUKSJON UNG"));
        assertThat(fnr1Response111First.getIdAlternativ(), equalTo(fnrLege1));
        assertThat(fnr1Response111Second.getIdAlternativ(), equalTo("311272976"));

        assertThat(fnr2Response110.getNavn(), equalTo("STORSKJERM TYDELIG SKRIVE"));
        assertThat(fnr2Response111First.getIdAlternativ(), equalTo(fnrLege2));
        assertThat(fnr2Response111Second.getIdAlternativ(), equalTo("735917527"));
    }

    private void stubTssConsumer(String expectedUri) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL resource = Resources.getResource("testdata/tss_response.json");
        Map<String, TssResponse> returnValue = objectMapper.readValue(resource, new TypeReference<Map<String, TssResponse>>() {
        });

        server.expect(requestToUriTemplate(expectedUri, avspillergruppeId, miljoe))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess(objectMapper.writeValueAsString(returnValue), MediaType.APPLICATION_JSON));
    }
}