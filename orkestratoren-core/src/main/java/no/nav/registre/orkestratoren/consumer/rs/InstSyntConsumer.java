package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.response.SletteInstitusjonsoppholdResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;

@Component
public class InstSyntConsumer {

    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE_START_SYNT = new ParameterizedTypeReference<Object>() {
    };
    private static final ParameterizedTypeReference<SletteInstitusjonsoppholdResponse> RESPONSE_TYPE_DELETE = new ParameterizedTypeReference<SletteInstitusjonsoppholdResponse>() {
    };
    private static final String NAV_CALL_ID = "orkestratoren";
    private static final String NAV_CONSUMER_ID = "orkestratoren";

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate startSyntetiseringUrl;
    private UriTemplate sletteIdenterUrl;

    public InstSyntConsumer(@Value("${testnorge-inst.rest-api.url}") String instServerUrl) {
        this.startSyntetiseringUrl = new UriTemplate(instServerUrl + "/v1/syntetisering/generer");
        this.sletteIdenterUrl = new UriTemplate(instServerUrl + "/v1/ident/identer?identer={identer}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "inst" })
    public Object startSyntetisering(SyntetiserInstRequest syntetiserInstRequest) {
        RequestEntity postRequest = RequestEntity.post(startSyntetiseringUrl.expand())
                .contentType(MediaType.APPLICATION_JSON)
                .header("navCallId", NAV_CALL_ID)
                .header("navConsumerId", NAV_CONSUMER_ID)
                .body(syntetiserInstRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE_START_SYNT);
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "inst" })
    public SletteInstitusjonsoppholdResponse slettIdenterFraInst(List<String> identer) {
        RequestEntity deleteRequest = RequestEntity.delete(sletteIdenterUrl.expand(convertListToString(identer)))
                .header("navCallId", NAV_CALL_ID)
                .header("navConsumerId", NAV_CONSUMER_ID)
                .build();
        return restTemplate.exchange(deleteRequest, RESPONSE_TYPE_DELETE).getBody();
    }

    private String convertListToString(List<String> list) {
        return String.join(",", list);
    }
}
