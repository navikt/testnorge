package no.nav.registre.tp.consumer.rs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import no.nav.registre.tp.provider.rs.request.SyntetiseringsRequest;

@Component
public class HodejegerenConsumer {

    private static final Integer MIN_AGE = 13;
    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };
    private static final ParameterizedTypeReference<Set<String>> RESPONSE_TYPE_SET = new ParameterizedTypeReference<Set<String>>() {
    };
    private final RestTemplate restTemplate;
    private final String hodejegerenUrl;
    private final String hodejegerenUrlAll;

    public HodejegerenConsumer(RestTemplate restTemplate, @Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenUrl) {
        this.restTemplate = restTemplate;
        this.hodejegerenUrl = hodejegerenUrl + "/v1/levende-identer/{avspillergruppeId}?miljoe={miljoe}&antallPersoner={antallPersoner}&minimumAlder={minimumAlder}";
        this.hodejegerenUrlAll = hodejegerenUrl + "/v1/alle-levende-identer/{avspillergruppeId}";
    }

    public List<String> getLivingIdentities(@Valid SyntetiseringsRequest request) {
        ResponseEntity<List<String>> responseEntity = restTemplate
                .exchange(hodejegerenUrl, HttpMethod.GET, null, RESPONSE_TYPE, request.getAvspillergruppeId(), request.getMiljoe(), request.getAntallPersoner(), MIN_AGE);

        List<String> fnrs = new ArrayList<>();

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            fnrs = responseEntity.getBody();
        }
        return fnrs;
    }

    public Set<String> getAllIdentities(@Valid SyntetiseringsRequest request) {
        ResponseEntity<Set<String>> response = restTemplate.exchange(hodejegerenUrlAll, HttpMethod.GET, null, RESPONSE_TYPE_SET, request.getAvspillergruppeId());
        Set<String> fnrs = new HashSet<>();

        if (response.getStatusCode() == HttpStatus.OK) {
            fnrs = response.getBody();
        }
        return fnrs;
    }

}
