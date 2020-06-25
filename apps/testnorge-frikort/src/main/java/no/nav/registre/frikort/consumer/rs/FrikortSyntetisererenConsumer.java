package no.nav.registre.frikort.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.List;
import java.util.Map;

import no.nav.registre.frikort.consumer.rs.response.SyntFrikortResponse;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;

@Component
@Slf4j
@DependencyOn(value = "nais-synthdata-frikort", external = true)
public class FrikortSyntetisererenConsumer {

    private RestTemplate restTemplate;
    private String syntServerUrl;

    public FrikortSyntetisererenConsumer(
            @Value("${synthdata.frikort.url}") String syntServerUrl,
            RestTemplate restTemplate
    ) {
        this.syntServerUrl = syntServerUrl;
        this.restTemplate = restTemplate;
    }

    public Map<String, List<SyntFrikortResponse>> hentSyntetiskeEgenandelerFraSyntRest(Map<String, Integer> request) {
        var postRequest = RequestEntity.post(URI.create(syntServerUrl + "/api/v1/generate")).body(request);

        try {
            return restTemplate.exchange(postRequest, new ParameterizedTypeReference<Map<String, List<SyntFrikortResponse>>>() {
            }).getBody();
        } catch (Exception e) {
            log.error("Uventet feil ved henting av syntetiske egenandeler fra synt-frikort.", e);
            throw e;
        }
    }
}