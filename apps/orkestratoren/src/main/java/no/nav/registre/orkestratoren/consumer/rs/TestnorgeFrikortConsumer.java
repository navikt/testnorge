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

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.response.GenererFrikortResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserFrikortRequest;

@Component
public class TestnorgeFrikortConsumer {

    private RestTemplate restTemplate;
    private UriTemplate url;

    public TestnorgeFrikortConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-frikort.rest.api.url}") String frikortServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = new UriTemplate(frikortServerUrl + "/v1/syntetisering/generer?leggPaaKoe=true");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "frikort" })
    public List<GenererFrikortResponse> startSyntetisering(
            SyntetiserFrikortRequest syntetiserFrikortRequest
    ) {
        var postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserFrikortRequest);
        List<GenererFrikortResponse> responseBody = null;
        var response = restTemplate.exchange(postRequest, new ParameterizedTypeReference<List<GenererFrikortResponse>>() {
        });
        if (response.getStatusCode().is2xxSuccessful()) {
            responseBody = response.getBody();
        }
        return responseBody;
    }
}
