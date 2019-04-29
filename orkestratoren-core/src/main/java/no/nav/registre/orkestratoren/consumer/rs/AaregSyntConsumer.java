package no.nav.registre.orkestratoren.consumer.rs;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import io.micrometer.core.annotation.Timed;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;

@Component
public class AaregSyntConsumer {

    private static final ParameterizedTypeReference<ResponseEntity> RESPONSE_TYPE = new ParameterizedTypeReference<ResponseEntity>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public AaregSyntConsumer(@Value("${testnorge-aareg.rest-api.url}") String aaregServerUrl) {
        this.url = new UriTemplate(aaregServerUrl + "/v1/syntetisering/generer?lagreIAareg={lagreIAareg}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "aareg" })
    public ResponseEntity startSyntetisering(SyntetiserAaregRequest syntetiserAaregRequest, boolean lagreIAareg) {
        RequestEntity postRequest = RequestEntity.post(url.expand(lagreIAareg)).body(syntetiserAaregRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}
