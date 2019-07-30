package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInntektsmeldingRequest;

@Slf4j
@Component
public class ArenaInntektSyntConsumer {

    private static final ParameterizedTypeReference<String> RESPONSE_TYPE = new ParameterizedTypeReference<String>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public ArenaInntektSyntConsumer(@Value("${testnorge-arena-inntekt.rest-api.url}") String arenaInntektServerUrl) {
        this.url = new UriTemplate(arenaInntektServerUrl + "/v1/syntetisering/generer");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "arena-inntekt" })
    public String startSyntetisering(SyntetiserInntektsmeldingRequest syntetiserInntektsmeldingRequest) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserInntektsmeldingRequest);
        String id = "";
        ResponseEntity<String> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);
        if (response != null && response.getBody() != null) {
            id = response.getBody();
        } else {
            log.error("Kunne ikke hente response body fra testnorge-arena-inntekt: NullPointerException");
        }
        return id;
    }
}
