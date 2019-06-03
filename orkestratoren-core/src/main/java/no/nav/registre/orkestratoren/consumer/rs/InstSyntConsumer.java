package no.nav.registre.orkestratoren.consumer.rs;

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

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;

@Component
public class InstSyntConsumer {

    private static final ParameterizedTypeReference<List<ResponseEntity>> RESPONSE_TYPE = new ParameterizedTypeReference<List<ResponseEntity>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public InstSyntConsumer(@Value("${testnorge-inst.rest-api.url}") String instServerUrl) {
        this.url = new UriTemplate(instServerUrl + "/v1/syntetisering/generer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "inst" })
    public ResponseEntity startSyntetisering(SyntetiserInstRequest syntetiserInstRequest) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserInstRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}
