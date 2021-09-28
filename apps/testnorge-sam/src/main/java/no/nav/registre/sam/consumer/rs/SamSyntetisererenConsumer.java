package no.nav.registre.sam.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.sam.domain.SyntetisertSamordningsmelding;

@Component
@Slf4j
public class SamSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<SyntetisertSamordningsmelding>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate;

    private final UriTemplate url;

    public SamSyntetisererenConsumer(RestTemplate restTemplate, @Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.restTemplate = restTemplate;
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generate/sam?numToGenerate={numToGenerate}");
    }

    @Timed(value = "sam.resource.latency", extraTags = {"operation", "sam-syntetisereren"})
    public List<SyntetisertSamordningsmelding> hentSammeldingerFromSyntRest(
            int numToGenerate
    ) {
        var getRequest = RequestEntity.get(url.expand(numToGenerate)).build();

        List<SyntetisertSamordningsmelding> syntetiserteMeldinger = new ArrayList<>();

        var response = restTemplate.exchange(getRequest, RESPONSE_TYPE).getBody();
        if (response != null) {
            syntetiserteMeldinger.addAll(response);
        } else {
            log.error("Kunne ikke hente response body fra synthdata-sam: NullPointerException");
        }

        return syntetiserteMeldinger;
    }
}
