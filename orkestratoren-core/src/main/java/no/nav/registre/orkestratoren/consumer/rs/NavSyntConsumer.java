package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.response.RsPureXmlMessageResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserNavmeldingerRequest;

@Component
public class NavSyntConsumer {

    private static final ParameterizedTypeReference<List<RsPureXmlMessageResponse>> RESPONSE_TYPE = new ParameterizedTypeReference<List<RsPureXmlMessageResponse>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public NavSyntConsumer(@Value("${testnorge-nav-endringsmeldinger.rest-api.url}") String skdServerUrl) {
        this.url = new UriTemplate(skdServerUrl + "/v1/syntetisering/generer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "nav-endringsmeldinger" })
    public ResponseEntity<List<RsPureXmlMessageResponse>> startSyntetisering(SyntetiserNavmeldingerRequest syntetiserNavmeldingerRequest) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserNavmeldingerRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}
