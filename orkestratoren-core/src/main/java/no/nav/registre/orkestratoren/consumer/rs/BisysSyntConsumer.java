package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;

@Component
public class BisysSyntConsumer {

    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE = new ParameterizedTypeReference<Object>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate url;

    public BisysSyntConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-bisys.rest-api.url}") String bisysServerUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = new UriTemplate(bisysServerUrl + "/v1/syntetisering/generer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "bisys" })
    public Object startSyntetisering(SyntetiserBisysRequest syntetiserBisysRequest) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserBisysRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}