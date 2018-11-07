package no.nav.registre.hodejegeren.consumer;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestToUriTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
@RestClientTest(TpsfConsumer.class)
@ActiveProfiles("itest")
public class TpsfConsumerTest {
    
    @Autowired
    private TpsfConsumer tpsfConsumer;
    @Autowired
    private MockRestServiceServer server;
    @Value("${tps-forvalteren.rest-api.url}")
    private String serverUrl;
    
    /**
     * Tester om konsumenten bygger korrekt URI og queryParam.
     *
     * @throws IOException
     */
    @Test
    public void shouldWriteProperRequestWhenGettingTpsServiceRoutine() throws IOException {
        Map map = new HashMap();
        String aksjonskode = "A0";
        String environment = "env";
        String fnr = "bla";
        
        String rutinenavn = "a";
        String expectedUri = serverUrl + "/v1/serviceroutine/{routineName}?aksjonsKode={aksjonskode}&environment={environment}&fnr={fnr}";
        
        this.server.expect(requestToUriTemplate(expectedUri, rutinenavn, aksjonskode, environment, fnr))
                .andRespond(request -> new MockClientHttpResponse("[]".getBytes(), HttpStatus.OK));
        
        tpsfConsumer.getTpsServiceRoutine(rutinenavn, aksjonskode, environment, fnr);
    }
}