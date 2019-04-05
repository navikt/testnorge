package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Set;

@Slf4j
@Component
public class TpConsumer {

    private final RestTemplate restTemplate;
    private final String tpUrl;

    public TpConsumer(RestTemplate restTemplate, @Value("${testnorge-tp.rest.api.url}") String tpUrl) {
        this.restTemplate = restTemplate;
        this.tpUrl = tpUrl;
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
    public Set<Object> findFnrs(String environment) {
        UriTemplate uriTemplate = new UriTemplate(tpUrl + "/orkestrering/personer/{miljoe}");
        return restTemplate.getForObject(uriTemplate.expand(environment), Set.class);
    }
}
