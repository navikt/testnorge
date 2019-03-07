package no.nav.registre.sam.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class SamSyntetisererenConsumer {
    private static final ParameterizedTypeReference<List<Map<String, String>>> RESPONSE_TYPE = new ParameterizedTypeReference<List<Map<String, String>>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public SamSyntetisererenConsumer(@Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generate/sam?numToGenerate={numToGenerate}");
    }

    @Timed(value = "sam.resource.latency", extraTags = { "operation", "sam-syntetisereren" })
    public List<Map<String, String>> hentSammeldingerFromSyntRest(int numToGenerate) {
        RequestEntity getRequest = RequestEntity.get(url.expand(numToGenerate)).build();

        List<Map<String, String>> syntetiserteMeldinger = new ArrayList<>();

        ResponseEntity<List<Map<String, String>>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE);
        if (response != null && response.getBody() != null) {
            syntetiserteMeldinger.addAll(response.getBody());
        } else {
            log.error("Kunne ikke hente response body fra synthdata-sam: NullPointerException");
        }

        return syntetiserteMeldinger;
    }
    
}
