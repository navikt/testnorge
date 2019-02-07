package no.nav.registre.orkestratoren.consumer.rs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import io.micrometer.core.annotation.Timed;

@Component
public class PoppSyntConsumer {

    private static final ParameterizedTypeReference<List<HttpStatus>> RESPONSE_TYPE = new ParameterizedTypeReference<List<HttpStatus>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public PoppSyntConsumer(@Value("${testnorge-sigrun.rest-api.url}") String sigrunServerUrl) {
        this.url = new UriTemplate(sigrunServerUrl + "/v1/syntetisering/generer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "sigrun" })
    public ResponseEntity startSyntetisering(int antallIdenter, String testdataEier) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).header("testdataEier", testdataEier).body(antallIdenter);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}
