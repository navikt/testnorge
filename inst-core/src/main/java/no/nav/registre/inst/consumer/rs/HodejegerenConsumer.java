package no.nav.registre.inst.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.inst.InstSaveInHodejegerenRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class HodejegerenConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate hentLevendeIdenterUrl;

    private UriTemplate hodejegerenSaveHistorikk;

    private static final ParameterizedTypeReference<Set<String>> RESPONSE_TYPE_SET = new ParameterizedTypeReference<Set<String>>() {
    };


    public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {
        this.hentLevendeIdenterUrl = new UriTemplate(hodejegerenServerUrl + "/v1/alle-levende-identer/{avspillergruppeId}");
        this.hodejegerenSaveHistorikk = new UriTemplate(hodejegerenServerUrl + "/v1/historikk/");
    }

    @Timed(value = "aareg.resource.latency", extraTags = {"operation", "hodejegeren"})
    public List<String> finnLevendeIdenter(Long avspillergruppeId) {
        RequestEntity getRequest = RequestEntity.get(hentLevendeIdenterUrl.expand(avspillergruppeId.toString())).build();
        List<String> levendeIdenter = new ArrayList<>();
        ResponseEntity<List<String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE);

        if (response.getBody() != null) {
            levendeIdenter.addAll(response.getBody());
        } else {
            log.error("HodejegerenConsumer.finnLevendeIdenter: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        }

        return levendeIdenter;
    }

    @Timed(value = "tp.resource.latency", extraTags = {"operation", "hodejegeren"})
    public Set<String> saveHistory(InstSaveInHodejegerenRequest request) {

        RequestEntity<InstSaveInHodejegerenRequest> postRequest = RequestEntity.post(hodejegerenSaveHistorikk.expand()).body(request);

        ResponseEntity<Set<String>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE_SET);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        return Collections.emptySet();
    }
}
