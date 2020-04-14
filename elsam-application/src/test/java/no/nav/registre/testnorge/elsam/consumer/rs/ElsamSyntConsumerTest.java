package no.nav.registre.testnorge.elsam.consumer.rs;

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

import no.nav.registre.testnorge.elsam.consumer.rs.response.synt.ElsamSyntResponse;

@RunWith(SpringRunner.class)
@RestClientTest(ElsamSyntConsumer.class)
@ActiveProfiles("test")
public class ElsamSyntConsumerTest {

    @Autowired
    private ElsamSyntConsumer elsamSyntConsumer;

    @Autowired
    private MockRestServiceServer server;

    @Value("${syntrest.rest.api.url}")
    private String serverUrl;

    @Test
    public void syntetiserSykemeldinger_OK() throws IOException {
        String fnr1 = "02128516002";
        String fnr2 = "30073245199";
        String expectedUri = serverUrl + "/v1/generate_sykmeldings_history";
        stubElsamSyntConsumer(expectedUri);

        //        elsamSyntConsumer.syntetiserSykemeldinger(Collections.singletonList(SykemeldingRequest.builder().build()));
        //
        //        assertThat(response.get(fnr1).size(), equalTo(6));
        //        assertThat(response.get(fnr2).size(), equalTo(6));
    }

    private void stubElsamSyntConsumer(String expectedUri) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        URL resource = Resources.getResource("testdata/elsam_synt_response.json");
        Map<String, ElsamSyntResponse> returnValue = objectMapper.readValue(resource, new TypeReference<Map<String, ElsamSyntResponse>>() {
        });

        server.expect(requestToUriTemplate(expectedUri))
                .andExpect(method(HttpMethod.POST))
                .andRespond(withSuccess(objectMapper.writeValueAsString(returnValue), MediaType.APPLICATION_JSON));
    }
}