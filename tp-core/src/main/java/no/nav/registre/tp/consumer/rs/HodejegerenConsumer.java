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
import java.util.List;

import no.nav.registre.tp.provider.rs.request.SyntetiseringsRequest;

@Component
public class HodejegerenConsumer {

    private static final Integer MIN_AGE = 13;
    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };
    private final RestTemplate restTemplate;
    private final String hodejegerenUrl;

    public HodejegerenConsumer(RestTemplate restTemplate, @Value("${testnorge-hodejegeren.rest-api.url}") String hodejegerenUrl) {
        this.restTemplate = restTemplate;
        this.hodejegerenUrl = hodejegerenUrl + "/v1/levende-identer/{avspillergruppeId}?miljoe={miljoe}&antallPersoner={antallPersoner}&minimumAlder={minimumAlder}";
    }

    public List<String> getFnrs(@Valid SyntetiseringsRequest request) {
        ResponseEntity<List<String>> responseEntity = restTemplate
                .exchange(hodejegerenUrl, HttpMethod.GET, null, RESPONSE_TYPE, request.getAvspillergruppeId(), request.getMiljoe(), request.getAntallPersoner(), MIN_AGE);

        List<String> fnrs = new ArrayList<>();

        if (responseEntity.getStatusCode() == HttpStatus.OK) {
            fnrs = responseEntity.getBody();
        }
        return fnrs;
    }

}
