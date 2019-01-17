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
public class TestnorgeSkdConsumer {

    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<List<Long>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public TestnorgeSkdConsumer(@Value("${testnorge-skd.rest-api.url}") String skdServerUrl) {
        this.url = new UriTemplate(skdServerUrl + "/v1/syntetisering/generer");
    }

    public List<Long> startSyntetisering(GenereringsOrdreRequest genereringsOrdreRequest) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).body(genereringsOrdreRequest);
        ArrayList<Long> ids = new ArrayList<>();
        ResponseEntity<List<Long>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);
        if (response != null && response.getBody() != null) {
            ids.addAll(response.getBody());
        } else {
            log.error("Kunne ikke hente response body fra Testnorge-Skd: NullPointerException");
        }
        return ids;
    }
}
