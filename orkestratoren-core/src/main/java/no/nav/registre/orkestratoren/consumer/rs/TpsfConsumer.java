package no.nav.registre.orkestratoren.consumer.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.orkestratoren.consumer.rs.requests.SendToTpsRequest;
import no.nav.registre.orkestratoren.consumer.rs.response.AvspillingResponse;

@Component
public class TpsfConsumer {

    @Autowired
    private RestTemplate restTemplateTpsf;

    private UriTemplate uriTemplate;

    public TpsfConsumer(@Value("${tps-forvalteren.rest-api.url}") String tpsfServerUrl) {
        uriTemplate = new UriTemplate(tpsfServerUrl + "/v1/endringsmelding/skd/send/{skdMeldingGruppeId}");
    }

    public AvspillingResponse sendSkdMeldingTilTpsf(Long skdMeldingGruppeId, SendToTpsRequest sendToTpsRequest) {
        String url = uriTemplate.expand(skdMeldingGruppeId).toString();

        return restTemplateTpsf.postForObject(url,
                sendToTpsRequest,
                AvspillingResponse.class);
    }
}
