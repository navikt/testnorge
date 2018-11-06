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

    @Value("${tpsf.base.url}")
    private String tpsfBaseUrl;

    public AvspillingResponse sendSkdMeldingTilTpsf(Long skdMeldingGruppeId, SendToTpsRequest sendToTpsRequest) {
        String url = tpsfServerUrl + tpsfBaseUrl + skdMeldingGruppeId;

        return restTemplateTpsf.postForObject(url,
                sendToTpsRequest,
                AvspillingResponse.class);
    }
}
