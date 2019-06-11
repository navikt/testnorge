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

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;

@Component
public class BisysSyntConsumer {

    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE = new ParameterizedTypeReference<Object>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public BisysSyntConsumer(@Value("${testnorge-bisys.rest-api.url}") String bisysServerUrl) {
        this.url = new UriTemplate(bisysServerUrl + "/v1/syntetisering/generer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "bisys" })
    public Object startSyntetisering(SyntetiserBisysRequest syntetiserBisysRequest) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserBisysRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}