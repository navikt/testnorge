package no.nav.registre.orkestratoren.consumer.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.orkestratoren.consumer.rs.requests.SendToTpsRequest;
import no.nav.registre.orkestratoren.consumer.rs.response.AvspillingResponse;

@Component
public class TpsfConsumer {

    @Autowired
    private RestTemplate restTemplateTpsf;

    @Value("${tpsf.server.url}")
    private String tpsfServerUrl;

    @Value("${tpsf.send.url}")
    private String tpsfSendUrl;

    public AvspillingResponse sendSkdMeldingTilTpsf(long skdMeldingGruppeId, SendToTpsRequest sendToTpsRequest) {
        String url = tpsfServerUrl + tpsfSendUrl + skdMeldingGruppeId;

        return restTemplateTpsf.postForObject(url,
                sendToTpsRequest,
                AvspillingResponse.class);
    }
}
