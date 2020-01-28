package no.nav.registre.endringsmeldinger.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.endringsmeldinger.consumer.rs.requests.SendTilTpsRequest;
import no.nav.registre.endringsmeldinger.consumer.rs.responses.RsPureXmlMessageResponse;

@Component
public class TpsfConsumer {

    private static final ParameterizedTypeReference<RsPureXmlMessageResponse> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private RestTemplate restTemplate;

    private UriTemplate sendTilTpsUrl;

    public TpsfConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${tps-forvalteren.rest-api.url}") String serverUrl,
            @Value("${testnorges.ida.credential.tpsf.username}") String username,
            @Value("${testnorges.ida.credential.tpsf.password}") String password
    ) {
        this.sendTilTpsUrl = new UriTemplate(serverUrl + "/v1/xmlmelding");
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.getInterceptors().add(new BasicAuthenticationInterceptor(username, password));
    }

    public RsPureXmlMessageResponse sendEndringsmeldingTilTps(SendTilTpsRequest sendTilTpsRequest) {
        var postRequest = RequestEntity.post(sendTilTpsUrl.expand()).body(sendTilTpsRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }
}
