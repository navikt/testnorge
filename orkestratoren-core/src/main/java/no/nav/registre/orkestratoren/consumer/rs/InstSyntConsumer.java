package no.nav.registre.orkestratoren.consumer.rs;

import io.micrometer.core.annotation.Timed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.response.InstitusjonsoppholdResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteInstitusjonsoppholdResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;

@Component
public class InstSyntConsumer {

    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE_START_SYNT = new ParameterizedTypeReference<Object>() {
    };
    private static final ParameterizedTypeReference<List<InstitusjonsoppholdResponse>> RESPONSE_TYPE_DELETE = new ParameterizedTypeReference<List<InstitusjonsoppholdResponse>>() {
    };
    private static final String NAV_CALL_ID = "orkestratoren";
    private static final String NAV_CONSUMER_ID = "orkestratoren";
    private static final String MILJOE = "q2";

    private RestTemplate restTemplate;
    private UriTemplate startSyntetiseringUrl;
    private UriTemplate sletteIdenterUrl;

    public InstSyntConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-inst.rest-api.url}") String instServerUrl) {
        this.restTemplate = restTemplateBuilder.build();
        this.startSyntetiseringUrl = new UriTemplate(instServerUrl + "/v1/syntetisering/generer?miljoe={miljoe}");
        this.sletteIdenterUrl = new UriTemplate(instServerUrl + "/v1/ident/batch?miljoe={miljoe}&identer={identer}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "inst" })
    public Object startSyntetisering(SyntetiserInstRequest syntetiserInstRequest) {
        RequestEntity postRequest = RequestEntity.post(startSyntetiseringUrl.expand(MILJOE))
                .contentType(MediaType.APPLICATION_JSON)
                .header("navCallId", NAV_CALL_ID)
                .header("navConsumerId", NAV_CONSUMER_ID)
                .body(syntetiserInstRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE_START_SYNT);
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "inst" })
    public SletteInstitusjonsoppholdResponse slettIdenterFraInst(List<String> identer) {
        RequestEntity deleteRequest = RequestEntity.delete(sletteIdenterUrl.expand(MILJOE, convertListToString(identer)))
                .header("navCallId", NAV_CALL_ID)
                .header("navConsumerId", NAV_CONSUMER_ID)
                .build();
        List<InstitusjonsoppholdResponse> responseBody = restTemplate.exchange(deleteRequest, RESPONSE_TYPE_DELETE).getBody();
        assert responseBody != null;
        for (InstitusjonsoppholdResponse oppholdResponse : responseBody) {
            oppholdResponse.setInstitusjonsopphold(null);
        }
        return new SletteInstitusjonsoppholdResponse(responseBody);
    }

    private String convertListToString(List<String> list) {
        return String.join(",", list);
    }
}
