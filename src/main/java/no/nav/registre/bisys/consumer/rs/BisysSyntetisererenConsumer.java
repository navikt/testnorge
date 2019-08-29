package no.nav.registre.bisys.consumer.rs;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.bisys.consumer.rs.responses.SyntetisertBidragsmelding;

@Slf4j
public class BisysSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<SyntetisertBidragsmelding>> RESPONSE_TYPE = new ParameterizedTypeReference<List<SyntetisertBidragsmelding>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public BisysSyntetisererenConsumer(String syntrestServerUrl) {
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generate/bisys?numToGenerate={antallMeldinger}");
    }

    @Timed(value = "bisys.resource.latency", extraTags = { "operation", "bisys-syntetisereren" })
    public List<SyntetisertBidragsmelding> getSyntetiserteBidragsmeldinger(int antallMeldinger) {
        List<SyntetisertBidragsmelding> syntetiserteMeldinger = new ArrayList<>();

        RequestEntity getRequest = RequestEntity.get(url.expand(antallMeldinger)).build();
        ResponseEntity<List<SyntetisertBidragsmelding>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE);

        if (response != null && response.getBody() != null) {
            syntetiserteMeldinger.addAll(response.getBody());
        } else {
            log.error("Kunne ikke hente response body fra synthdata-bisys: NullPointerException");
        }

        return syntetiserteMeldinger;
    }
}
