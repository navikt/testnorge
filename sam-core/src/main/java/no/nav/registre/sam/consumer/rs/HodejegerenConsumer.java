package no.nav.registre.sam.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sam.SamSaveInHodejegerenRequest;
import no.nav.registre.sam.provider.rs.requests.SyntetiserSamRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class HodejegerenConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };

    private static final ParameterizedTypeReference<Set<String>> RESPONSE_TYPE_SET = new ParameterizedTypeReference<Set<String>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate hentLevendeIdenterUrl;

    private UriTemplate hodejegerenSaveHistorikk;

    public HodejegerenConsumer(@Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenServerUrl) {
        this.hentLevendeIdenterUrl = new UriTemplate(hodejegerenServerUrl +
                "/v1/levende-identer/{avspillergruppeId}?miljoe={miljoe}&antallPersoner={antallIdenter}");
        this.hodejegerenSaveHistorikk = new UriTemplate(hodejegerenServerUrl + "/v1/historikk");
    }

    @Timed(value = "sam.resource.latency", extraTags = {"operation", "hodejegeren"})
    public List<String> finnLevendeIdenter(SyntetiserSamRequest syntetiserSamRequest) {
        RequestEntity getRequest = RequestEntity.get(hentLevendeIdenterUrl.expand(
                syntetiserSamRequest.getAvspillergruppeId(),
                syntetiserSamRequest.getMiljoe(),
                syntetiserSamRequest.getAntallMeldinger()))
                .build();

        List<String> identer = new ArrayList<>();
        ResponseEntity<List<String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE);

        if (response.getBody() != null) {
            identer.addAll(response.getBody());
        } else {
            log.error("HodejegerenConsumer.finnLevendeIdenter: Kunne ikke hente response body fra Hodejegeren: NullPointerException");
        }

        return identer;
    }

    @Timed(value = "sam.resource.latency", extraTags = {"operation", "hodejegeren"})
    public Set<String> saveHistory(SamSaveInHodejegerenRequest request) {

        RequestEntity<SamSaveInHodejegerenRequest> postRequest = RequestEntity.post(hodejegerenSaveHistorikk.expand()).body(request);

        ResponseEntity<Set<String>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE_SET);

        if (!response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        }
        return Collections.emptySet();
    }
}
