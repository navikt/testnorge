package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserSamRequest;

import java.util.List;

@Component
public class TestnorgeSamConsumer {

    private static final ParameterizedTypeReference<List<Object>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate url;

    public TestnorgeSamConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-sam.rest.api.url}") String samServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = new UriTemplate(samServerUrl + "/v1/syntetisering/generer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "sam" })
    public ResponseEntity<List<Object>> startSyntetisering(
            SyntetiserSamRequest syntetiserSamRequest
    ) {
        var postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserSamRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}
