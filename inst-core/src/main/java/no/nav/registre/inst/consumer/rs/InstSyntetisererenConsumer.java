package no.nav.registre.inst.consumer.rs;

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
public class InstSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<Map<String, String>>> RESPONSE_TYPE = new ParameterizedTypeReference<List<Map<String, String>>>() {};

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public InstSyntetisererenConsumer(@Value("${syntrest.rest.inst.api.url}") String syntrestServerUrl) {
        this.url = new UriTemplate(syntrestServerUrl);
    }

    public List<Map<String, String>> hentInstMeldingerFromSyntRest(int numToGenerate) {
        RequestEntity getRequest = RequestEntity.get(url.expand(numToGenerate)).build();

        List<Map<String, String>> syntetiserteMeldinger = new ArrayList<>();

        ResponseEntity<List<Map<String, String>>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE);

        if (response != null && response.getBody() != null) {
            syntetiserteMeldinger.addAll(response.getBody());
        } else {
            log.error("Kunne ikke hente response body fra synthdata-inst: NullPointerException");
        }

        return syntetiserteMeldinger;
    }
}
