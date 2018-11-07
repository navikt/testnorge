package no.nav.registre.hodejegeren.consumer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;

@RunWith(SpringRunner.class)
@RestClientTest(TpsSyntetisererenConsumer.class)
@ActiveProfiles("itest")
public class TpsSyntetisererenConsumerTest {
    
    @Autowired
    private TpsSyntetisererenConsumer consumer;
    @Autowired
    private MockRestServiceServer server;
    @Value("${tps-syntetisereren.rest-api.url}")
    private String serverUrl;
    
    @Test
    public void testKonsumeringAvTpsSynt() {
        String endringskode = "02";
        int antallMeldinger = 1;
        this.server.expect(requestToUriTemplate(serverUrl +
                "/generate?endringskode={endringskode}&antallMeldinger={antall}", endringskode, antallMeldinger))
                .andRespond(withSuccess("[null]", MediaType.APPLICATION_JSON));
        consumer.getSyntetiserteSkdmeldinger(endringskode, antallMeldinger);
    }
}