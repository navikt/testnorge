package no.nav.registre.orkestratoren.consumer.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import io.micrometer.core.annotation.Timed;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;

@Component
public class SamSyntConsumer {

    private static final ParameterizedTypeReference<ResponseEntity> RESPONSE_TYPE = new ParameterizedTypeReference<ResponseEntity>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public SamSyntConsumer(@Value("${testnorge-sam.rest-api.url}") String samServerUrl) {
        this.url = new UriTemplate(samServerUrl + "/v1/syntetisering/generer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "sam" })
    public ResponseEntity startSyntetisering(SyntetiserSamRequest syntetiserSamRequest) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserSamRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}
