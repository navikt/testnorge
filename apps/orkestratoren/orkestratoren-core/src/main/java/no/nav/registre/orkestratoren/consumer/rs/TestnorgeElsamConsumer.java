package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserElsamRequest;

@Component
@Slf4j
public class TestnorgeElsamConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate url;

    public TestnorgeElsamConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-elsam.rest.api.url}") String baseUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = new UriTemplate(baseUrl + "/v1/syntetisering/generer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "elsam" })
    public List<String> startSyntetisering(
            SyntetiserElsamRequest syntetiserElsamRequest
    ) {
        var postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserElsamRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }
}