package no.nav.registre.orkestratoren.consumer.rs;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserEiaRequest;

@Component
@Slf4j
public class EiaSyntConsumer {

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public EiaSyntConsumer(@Value("${eias-emottakstub.rest-api.url}") String baseUrl, @Value("${eiabatch.queue}") String queueName) {
        this.url = new UriTemplate(baseUrl + "/v1/syntetisering/generer/" + queueName);
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "eia" })
    public List<String> startSyntetisering(SyntetiserEiaRequest syntetiserEiaRequest) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserEiaRequest);
        ArrayList<String> identer = new ArrayList<>();
        ResponseEntity<List<String>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);
        if (response != null && response.getBody() != null) {
            identer.addAll(response.getBody());
        } else {
            log.error("Kunne ikke hente response body fra eias-emottakstub: NullPointerException");
        }
        return identer;
    }
}