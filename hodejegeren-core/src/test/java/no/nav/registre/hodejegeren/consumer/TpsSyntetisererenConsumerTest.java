package no.nav.registre.hodejegeren.consumer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.queryParam;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.client.MockClientHttpResponse;
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
    @Ignore
    public void testKonsumeringAvTpsSynt() {
        this.server.expect(requestTo(serverUrl + "/generate"))
                .andExpect(queryParam("endringskode", "02"))
                .andExpect(queryParam("antallMeldinger", "1"))
                .andRespond(request -> new MockClientHttpResponse("[]".getBytes(), HttpStatus.OK));
        consumer.getSyntetiserteSkdmeldinger("02", 1);
    }
}