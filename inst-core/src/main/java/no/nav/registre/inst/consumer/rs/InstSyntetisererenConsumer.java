package no.nav.registre.inst.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.Institusjonsforholdsmelding;
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
public class InstSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<Institusjonsforholdsmelding>> RESPONSE_TYPE = new ParameterizedTypeReference<List<Institusjonsforholdsmelding>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public InstSyntetisererenConsumer(@Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generate/inst?numToGenerate={numToGenerate}");
    }

    @Timed(value = "inst.resource.latency", extraTags = { "operation", "inst-syntetisereren" })
    public List<Institusjonsforholdsmelding> hentInstMeldingerFromSyntRest(int numToGenerate) {
        RequestEntity getRequest = RequestEntity.get(url.expand(numToGenerate)).build();

        List<Institusjonsforholdsmelding> syntetiserteMeldinger = new ArrayList<>();

        ResponseEntity<List<Institusjonsforholdsmelding>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE);
        if (response != null && response.getBody() != null) {
            syntetiserteMeldinger.addAll(response.getBody());
        } else {
            log.error("Kunne ikke hente response body fra synthdata-inst: NullPointerException");
        }

        return syntetiserteMeldinger;
    }
}
