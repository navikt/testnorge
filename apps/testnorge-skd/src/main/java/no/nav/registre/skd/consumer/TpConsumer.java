package no.nav.registre.skd.consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;


@Component
public class TpConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private final UriTemplate leggTilIdentUrl;

    public TpConsumer(@Value("${testnorge-tp.rest-api.url}") String serverUrl) {
        this.leggTilIdentUrl = new UriTemplate(serverUrl + "/v1/orkestrering/opprettPersoner/{miljoe}");
    }

    public ResponseEntity<List<String>> leggTilIdenterITp(List<String> identer, String miljoe) {
        var postRequest = RequestEntity.post(leggTilIdentUrl.expand(miljoe)).header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE).body(identer);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}
