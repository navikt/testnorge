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

import no.nav.registre.aareg.consumer.rs.responses.ArbeidsforholdsResponse;

@Component
@Slf4j
public class AaregSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<ArbeidsforholdsResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<List<ArbeidsforholdsResponse>>() {
    };

    @Value("${aareg.pageSize}")
    private int pageSize;

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public AaregSyntetisererenConsumer(@Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generate/aareg");
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "aareg-syntetisereren" })
    public List<ArbeidsforholdsResponse> getSyntetiserteArbeidsforholdsmeldinger(List<String> identer) {
        List<ArbeidsforholdsResponse> syntetiserteMeldinger = new ArrayList<>();
        RequestEntity postRequest;
        ResponseEntity<List<ArbeidsforholdsResponse>> response;

        if (identer.size() > pageSize) {
            for (int i = 0; i * pageSize < identer.size(); i++) {
                int endIndex = pageSize * (i + 1);
                if (endIndex > identer.size()) {
                    endIndex = identer.size();
                }

                postRequest = RequestEntity.post(url.expand()).body(identer.subList(i * pageSize, endIndex));

                response = restTemplate.exchange(postRequest, RESPONSE_TYPE);
                if (response.getBody() != null) {
                    syntetiserteMeldinger.addAll(response.getBody());
                } else {
                    log.error("Kunne ikke hente response body fra synthdata-aareg: NullPointerException");
                }
            }
        } else {
            postRequest = RequestEntity.post(url.expand()).body(identer);

            response = restTemplate.exchange(postRequest, RESPONSE_TYPE);
            if (response.getBody() != null) {
                syntetiserteMeldinger.addAll(response.getBody());
            } else {
                log.error("Kunne ikke hente response body fra synthdata-aareg: NullPointerException");
            }
        }

        return syntetiserteMeldinger;
    }
}
