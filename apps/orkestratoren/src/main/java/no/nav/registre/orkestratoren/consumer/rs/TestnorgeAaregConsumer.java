package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.response.SletteArbeidsforholdResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserAaregRequest;

@Slf4j
@Component
public class TestnorgeAaregConsumer {

    private static final ParameterizedTypeReference<List<Object>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };
    private static final ParameterizedTypeReference<SletteArbeidsforholdResponse> RESPONSE_TYPE_DELETE = new ParameterizedTypeReference<>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate startSyntetiseringUrl;
    private UriTemplate slettIdenterUrl;

    public TestnorgeAaregConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-aareg.rest.api.url}") String aaregServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.startSyntetiseringUrl = new UriTemplate(aaregServerUrl + "/v1/syntetisering/generer?sendAlleEksisterende={sendAlleEksisterende}");
        this.slettIdenterUrl = new UriTemplate(aaregServerUrl + "/v1/ident");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "aareg" })
    public ResponseEntity<List<Object>> startSyntetisering(
            SyntetiserAaregRequest syntetiserAaregRequest,
            boolean sendAlleEksisterende
    ) {
        var postRequest = RequestEntity.post(startSyntetiseringUrl.expand(sendAlleEksisterende)).contentType(MediaType.APPLICATION_JSON).body(syntetiserAaregRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }
}
