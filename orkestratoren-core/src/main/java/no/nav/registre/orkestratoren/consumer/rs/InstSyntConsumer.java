package no.nav.registre.orkestratoren.consumer.rs;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import io.micrometer.core.annotation.Timed;

import no.nav.registre.orkestratoren.consumer.rs.response.SletteInstitusjonsoppholdResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;

@Component
public class InstSyntConsumer {

    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE_START_SYNT = new ParameterizedTypeReference<Object>() {
    };
    private static final ParameterizedTypeReference<SletteInstitusjonsoppholdResponse> RESPONSE_TYPE_DELETE = new ParameterizedTypeReference<SletteInstitusjonsoppholdResponse>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate startSyntetiseringUrl;
    private UriTemplate sletteIdenterUrl;

    public InstSyntConsumer(@Value("${testnorge-inst.rest-api.url}") String instServerUrl) {
        this.startSyntetiseringUrl = new UriTemplate(instServerUrl + "/v1/syntetisering/generer");
        this.sletteIdenterUrl = new UriTemplate(instServerUrl + "/v1/ident");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "inst" })
    public Object startSyntetisering(SyntetiserInstRequest syntetiserInstRequest) {
        RequestEntity postRequest = RequestEntity.post(startSyntetiseringUrl.expand()).contentType(MediaType.APPLICATION_JSON).body(syntetiserInstRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE_START_SYNT);
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "inst" })
    public SletteInstitusjonsoppholdResponse slettIdenterFraInst(List<String> identer) {
        RequestEntity deleteRequest = RequestEntity.method(HttpMethod.DELETE, sletteIdenterUrl.expand()).contentType(MediaType.APPLICATION_JSON).body(identer);
        return restTemplate.exchange(deleteRequest, RESPONSE_TYPE_DELETE).getBody();
    }
}
