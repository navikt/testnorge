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
import java.util.Map;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@Slf4j
@Component
public class InntektSyntConsumer {

    private static final ParameterizedTypeReference<Map<String, List<Object>>> RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, List<Object>>>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate url;

    public InntektSyntConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-inntekt.rest.api.url}") String inntektServerUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = new UriTemplate(inntektServerUrl + "/v1/syntetisering/generer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "inntekt" })
    public Map<String, List<Object>> startSyntetisering(SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserInntektsmeldingRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }
}
