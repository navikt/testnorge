package no.nav.registre.frikort.consumer.rs;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;

@Component
@Slf4j
public class SyntrestConsumer {

    private RestTemplate restTemplate;
    private String syntServerUrl;

    public SyntrestConsumer(
            @Value("${syntrest.api.url}") String syntServerUrl,
            RestTemplate restTemplate
    ) {
        this.syntServerUrl = syntServerUrl;
        this.restTemplate = restTemplate;
    }

    public Map<String, List<SyntFrikortResponse>> hentSyntetiskeEgenandelerFraSyntRest(Map<String, Integer> request) {
        var postRequest = RequestEntity.post(URI.create(syntServerUrl + "/v1/generate/frikort"))
                .header(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON_VALUE)
                .body(request);

        try {
            return restTemplate.exchange(postRequest, new ParameterizedTypeReference<Map<String, List<SyntFrikortResponse>>>() {
            }).getBody();
        } catch (Exception e) {
            log.error("Uventet feil ved henting av syntetiske egenandeler fra syntrest.", e);
            throw e;
        }
    }
}