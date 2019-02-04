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

import java.util.ArrayList;
import java.util.List;

@Component
@Slf4j
public class AaregSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public AaregSyntetisererenConsumer(@Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generate_aareg");
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "aareg-syntetisereren" })
    public List<String> getSyntetiserteMeldinger(List<String> identer) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).body(identer);

        List<String> syntetiserteMeldinger = new ArrayList<>();

        ResponseEntity<List<String>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);
        if (response != null && response.getBody() != null) {
            syntetiserteMeldinger.addAll(response.getBody());
        } else {
            log.error("Kunne ikke hente response body fra synthdata-aareg: NullPointerException");
        }

        return syntetiserteMeldinger;
    }
}
