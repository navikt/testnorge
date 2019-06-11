package no.nav.registre.orkestratoren.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;

@Component
@Slf4j
public class TestnorgeTpConsumer {

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public TestnorgeTpConsumer(@Value("${testnorge-tp.rest-api.url}") String baseUrl) {
        this.url = new UriTemplate(baseUrl + "/v1/syntetisering/generer/");
    }

    public ResponseEntity startSyntetisering(SyntetiserTpRequest request) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(request);
        ResponseEntity<String> response = restTemplate.exchange(postRequest, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Klarte ikke syntetisere tp");
        }
        return response;
    }
}
