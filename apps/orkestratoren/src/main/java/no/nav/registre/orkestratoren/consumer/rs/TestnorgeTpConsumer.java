package no.nav.registre.orkestratoren.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserTpRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

@Component
@Slf4j
public class TestnorgeTpConsumer {

    private RestTemplate restTemplate;
    private UriTemplate url;

    public TestnorgeTpConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-tp.rest.api.url}") String baseUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = new UriTemplate(baseUrl + "/v1/syntetisering/generer/");
    }

    public ResponseEntity<String> startSyntetisering(
            SyntetiserTpRequest request
    ) {
        var postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(request);
        var response = restTemplate.exchange(postRequest, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.error("Klarte ikke syntetisere tp");
        }
        return response;
    }
}
