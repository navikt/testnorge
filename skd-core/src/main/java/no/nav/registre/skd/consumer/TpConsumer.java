package no.nav.registre.skd.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

@Component
public class TpConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate leggTilIdentUrl;

    public TpConsumer(@Value("${testnorge-tp.rest-api.url}") String serverUrl) {
        this.leggTilIdentUrl = new UriTemplate(serverUrl + "/v1/orkestrering/opprettPersoner/{miljoe}");
    }

    public ResponseEntity<List<String>> leggTilIdenterITp(List<String> identer, String miljoe) {
        RequestEntity postRequest = RequestEntity.post(leggTilIdentUrl.expand(miljoe)).body(identer);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}
