package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserMedlRequest;

@Component
public class MedlConsumer {

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public MedlConsumer(@Value("${testnorge-medl.rest-api.url}") String baseUrl) {
        this.url = new UriTemplate(baseUrl + "/v1/syntetisering/generer/");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "medl" })
    public Object startSyntetisering(SyntetiserMedlRequest syntetiserMedlRequest) {
        RequestEntity postRequest = RequestEntity.post(url.expand())
                .header("Content-Type", "application/json")
                .body(syntetiserMedlRequest);
        return restTemplate.exchange(postRequest, Object.class).getBody();
    }
}
