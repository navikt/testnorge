package no.nav.registre.sdforvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.Set;


@Slf4j
@Component
public class TpConsumer {

    private final RestTemplate restTemplate;
    private final String tpUrl;

    private static final ParameterizedTypeReference<Set<String>> RESPONSE_TYPE_SET = new ParameterizedTypeReference<Set<String>>() {
    };

    public TpConsumer(RestTemplate restTemplate, @Value("${testnorge.tp.rest.api.url}") String tpUrl) {
        this.restTemplate = restTemplate;
        this.tpUrl = tpUrl + "/v1";
    }

    /**
     * @param data        Fnr som skal legges til i tp
     * @param environment Miljøet de skal legges til i
     * @return true hvis den ble lagret i tp, false hvis de ikke ble lagret
     */
    public boolean send(Set<String> data, String environment) {
        UriTemplate uriTemplate = new UriTemplate(tpUrl + "/orkestrering/opprettPersoner/{miljoe}");
        RequestEntity<Set<String>> requestEntity = new RequestEntity<>(data, HttpMethod.POST, uriTemplate.expand(environment));
        ResponseEntity<Set> responseEntity = restTemplate.exchange(requestEntity, Set.class);
        if (responseEntity.getStatusCode() != HttpStatus.OK) {
            log.warn("Noe skjedde med initialisering av TP i gitt miljø. Det kan være at databasen ikke er koblet opp til miljø {}", environment);
            return false;
        }
        return true;
    }

    /**
     * @param environment Miljøet som skal brukes for å finne fnr
     * @return Et set med fnr som finnes i gitt miljø
     */
    @SuppressWarnings("Duplicates")
    public Set<String> findFnrs(String environment) {
        UriTemplate uriTemplate = new UriTemplate(tpUrl + "/orkestrering/personer/{miljoe}");
        ResponseEntity<Set<String>> response = restTemplate.exchange(uriTemplate.expand(environment), HttpMethod.GET, null, RESPONSE_TYPE_SET);
        if (response.getBody() != null) {
            return response.getBody();
        }
        return Collections.emptySet();
    }
}
