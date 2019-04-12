package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Component
public class SamConsumer {

    private final RestTemplate restTemplate;
    private final String samUrl;

    private static final ParameterizedTypeReference<Set<String>> RESPONSE_TYPE_SET = new ParameterizedTypeReference<Set<String>>() {
    };

    public SamConsumer(RestTemplate restTemplate, @Value("${testnorge.sam.rest.api.url}") String samUrl) {
        this.restTemplate = restTemplate;
        this.samUrl = samUrl + "/v1";
    }

    public Set<String> findFnrs(String environment) {
        //TODO: Add sam
        UriTemplate uriTemplate = new UriTemplate(samUrl + "/");
        ResponseEntity<Set<String>> response = restTemplate.exchange(uriTemplate.expand(environment), HttpMethod.GET, null, RESPONSE_TYPE_SET);
        if (response.getBody() != null) {
            return response.getBody();
        }
        return Collections.emptySet();
    }

    public void send(Set<String> data, String environment) {

    }
}
