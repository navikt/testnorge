package no.nav.registre.orkestratoren.consumer.rs;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;

@Component
public class HodejegerenConsumer {

    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<List<Long>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {
        this.url = new UriTemplate(hodejegerenServerUrl + "/v1/syntetisering/generer");
    }

    public List<Long> startSyntetisering(GenereringsOrdreRequest genereringsOrdreRequest) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).body(genereringsOrdreRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }
}
