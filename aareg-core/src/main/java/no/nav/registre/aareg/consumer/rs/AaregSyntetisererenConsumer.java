package no.nav.registre.aareg.consumer.rs;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AaregSyntetisererenConsumer {

    private static final ParameterizedTypeReference<Map<String, List<Map<String, String>>>> RESPONSE_TYPE = new ParameterizedTypeReference<Map<String, List<Map<String, String>>>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public AaregSyntetisererenConsumer(@Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generateAareg");
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "aareg-syntetisereren" })
    public Map<String, List<Map<String, String>>> getSyntetiserteArbeidsforholdsmeldinger(List<String> identer) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).body(identer);

        Map<String, List<Map<String, String>>> syntetiserteMeldinger = new HashMap<>();

        ResponseEntity<Map<String, List<Map<String, String>>>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);
        if (response != null && response.getBody() != null) {
            syntetiserteMeldinger.putAll(response.getBody());
        } else {
            log.error("Kunne ikke hente response body fra synthdata-aareg: NullPointerException");
        }

        return syntetiserteMeldinger;
    }
}
