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

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserBisysRequest;

@Slf4j
@Component
public class TestnorgeBisysConsumer {

    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate url;

    public TestnorgeBisysConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-bisys.rest.api.url}") String bisysServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = new UriTemplate(bisysServerUrl + "/v1/syntetisering/genererOgLagre");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "bisys" })
    public Object startSyntetisering(
            SyntetiserBisysRequest syntetiserBisysRequest
    ) {
        log.info("Oppretter {} nye bidragsmeldinger", syntetiserBisysRequest.getAntallNyeIdenter());
        var postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserBisysRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}