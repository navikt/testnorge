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
public class AaregSyntConsumer {

    private static final ParameterizedTypeReference<ResponseEntity> RESPONSE_TYPE = new ParameterizedTypeReference<ResponseEntity>() {
    };
    private static final ParameterizedTypeReference<SletteArbeidsforholdResponse> RESPONSE_TYPE_DELETE = new ParameterizedTypeReference<SletteArbeidsforholdResponse>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate startSyntetiseringUrl;
    private UriTemplate slettIdenterUrl;

    public AaregSyntConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-aareg.rest-api.url}") String aaregServerUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.startSyntetiseringUrl = new UriTemplate(aaregServerUrl + "/v1/syntetisering/generer?lagreIAareg={lagreIAareg}");
        this.slettIdenterUrl = new UriTemplate(aaregServerUrl + "/v1/ident");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "aareg" })
    public ResponseEntity startSyntetisering(SyntetiserAaregRequest syntetiserAaregRequest, boolean lagreIAareg) {
        RequestEntity postRequest = RequestEntity.post(startSyntetiseringUrl.expand(lagreIAareg)).contentType(MediaType.APPLICATION_JSON).body(syntetiserAaregRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "aareg" })
    public SletteArbeidsforholdResponse slettIdenterFraAaregstub(List<String> identer) {
        RequestEntity deleteRequest = RequestEntity.method(HttpMethod.DELETE, slettIdenterUrl.expand())
                .contentType(MediaType.APPLICATION_JSON)
                .body(identer);
        SletteArbeidsforholdResponse response = SletteArbeidsforholdResponse.builder().build();
        try {
            response = restTemplate.exchange(deleteRequest, RESPONSE_TYPE_DELETE).getBody();
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke slette identer fra aaregstub", e);
            response.setIdenterSomIkkeKunneSlettes(identer);
        }
        return response;
    }
}
