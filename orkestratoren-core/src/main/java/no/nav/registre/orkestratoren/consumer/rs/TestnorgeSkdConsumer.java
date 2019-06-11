package no.nav.registre.orkestratoren.consumer.rs;

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

import no.nav.registre.orkestratoren.consumer.rs.requests.GenereringsOrdreRequest;
import no.nav.registre.orkestratoren.consumer.rs.response.SkdMeldingerTilTpsRespons;

@Component
@Slf4j
public class TestnorgeSkdConsumer {

    private static final ParameterizedTypeReference<SkdMeldingerTilTpsRespons> RESPONSE_TYPE = new ParameterizedTypeReference<SkdMeldingerTilTpsRespons>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public TestnorgeSkdConsumer(@Value("${testnorge-skd.rest-api.url}") String skdServerUrl) {
        this.url = new UriTemplate(skdServerUrl + "/v1/syntetisering/generer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "skd" })
    public ResponseEntity startSyntetisering(GenereringsOrdreRequest genereringsOrdreRequest) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(genereringsOrdreRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}
