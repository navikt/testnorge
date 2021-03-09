package no.nav.registre.sdforvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.Set;

@Slf4j
@Component
public class HodejegerenConsumer {

    private final RestTemplate restTemplate;
    private final String hodejegerenUrl;

    private static final ParameterizedTypeReference<Set<String>> RESPONSE_TYPE_SET = new ParameterizedTypeReference<Set<String>>() {
    };

    public HodejegerenConsumer(RestTemplate restTemplate, @Value("${testnorge.hodejegeren.rest.api.url}") String hodejegerenUrl) {
        this.restTemplate = restTemplate;
        this.hodejegerenUrl = hodejegerenUrl + "/v1";
    }

    /**
     * @param playgroupId AvspillergruppeId som man ønsker å hente fnr fra
     * @return Et set med fnr som eksisterer i gruppen
     */
    public Set<String> getPlaygroupFnrs(Long playgroupId) {
        UriTemplate uriTemplate = new UriTemplate(hodejegerenUrl + "/alle-identer/{avspillergruppeId}");
        return getFromHodejegeren(uriTemplate.expand(playgroupId));
    }

    public Set<String> getLivingFnrs(Long playgroupId, String environment) {
        UriTemplate uriTemplate = new UriTemplate(hodejegerenUrl + "/levende-identer/{avspillergruppeId}?miljoe={miljoe}");
        return getFromHodejegeren(uriTemplate.expand(playgroupId, environment));
    }

    private Set<String> getFromHodejegeren(URI queryPath) {
        ResponseEntity<Set<String>> response = restTemplate.exchange(queryPath, HttpMethod.GET, null, RESPONSE_TYPE_SET);
        if (response.getBody() != null) {
            return response.getBody();
        }
        return Collections.emptySet();
    }
}
