package no.nav.registre.bisys.consumer.rs;

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

import no.nav.registre.bisys.consumer.rs.responses.BidragsResponse;

@Component
@Slf4j
public class BisysSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<BidragsResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<List<BidragsResponse>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public BisysSyntetisererenConsumer(@Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generate/bisys?numToGenerate={antallMeldinger}");
    }

    @Timed(value = "bisys.resource.latency", extraTags = { "operation", "bisys-syntetisereren" })
    public List<BidragsResponse> getSyntetiserteBidragsmeldinger(int antallMeldinger) {
        List<BidragsResponse> syntetiserteMeldinger = new ArrayList<>();

        RequestEntity getRequest = RequestEntity.get(url.expand(antallMeldinger)).build();
        ResponseEntity<List<BidragsResponse>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE);

        if (response != null && response.getBody() != null) {
            syntetiserteMeldinger.addAll(response.getBody());
        } else {
            log.error("Kunne ikke hente response body fra synthdata-bisys: NullPointerException");
        }

        return syntetiserteMeldinger;
    }
}
