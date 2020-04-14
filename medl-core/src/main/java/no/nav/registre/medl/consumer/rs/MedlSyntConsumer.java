package no.nav.registre.medl.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.List;

import no.nav.registre.medl.consumer.rs.response.MedlSyntResponse;

@Component
@Slf4j
public class MedlSyntConsumer {

    private static final ParameterizedTypeReference<List<MedlSyntResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate;

    private UriTemplate url;

    public MedlSyntConsumer(RestTemplate restTemplate, @Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.restTemplate = restTemplate;
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generate/medl?numToGenerate={numToGenerate}");
    }

    @Timed(value = "medl.resource.latency", extraTags = { "operation", "medl-syntetisereren" })
    public List<MedlSyntResponse> hentMedlemskapsmeldingerFromSyntRest(
            int numToGenerate
    ) {
        var getRequest = RequestEntity.get(url.expand(numToGenerate)).build();
        var response = restTemplate.exchange(getRequest, RESPONSE_TYPE);
        if (response.getBody() != null && response.getStatusCode() == HttpStatus.OK) {
            return response.getBody();
        } else {
            log.error("Kunne ikke hente response body fra synthdata-medl: {}", response.getStatusCode());
        }

        return Collections.emptyList();
    }
}
