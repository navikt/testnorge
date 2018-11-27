package no.nav.registre.orkestratoren.consumer.rs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;

@Component
@Slf4j
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
        ArrayList<Long> ids = new ArrayList<>();
        ResponseEntity<List<Long>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);
        if (response != null && response.getBody() != null) {
            ids.addAll(response.getBody());
        } else {
            log.error("Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        }
        return ids;
    }
}
