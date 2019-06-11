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

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserPoppRequest;

@Component
public class PoppSyntConsumer {

    private static final ParameterizedTypeReference<List<Integer>> RESPONSE_TYPE = new ParameterizedTypeReference<List<Integer>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public PoppSyntConsumer(@Value("${testnorge-sigrun.rest-api.url}") String sigrunServerUrl) {
        this.url = new UriTemplate(sigrunServerUrl + "/v1/syntetisering/generer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "sigrun" })
    public ResponseEntity startSyntetisering(SyntetiserPoppRequest syntetiserPoppRequest, String testdataEier) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).header("testdataEier", testdataEier).contentType(MediaType.APPLICATION_JSON).body(syntetiserPoppRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}
