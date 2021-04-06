package no.nav.registre.orkestratoren.consumer.rs;

import com.google.common.collect.Lists;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.orkestratoren.consumer.rs.response.InstitusjonsoppholdResponse;
import no.nav.registre.orkestratoren.consumer.rs.response.SletteInstitusjonsoppholdResponse;
import no.nav.registre.orkestratoren.provider.rs.requests.SyntetiserInstRequest;

@Slf4j
@Component
public class TestnorgeInstConsumer {

    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE_START_SYNT = new ParameterizedTypeReference<>() {
    };

    private static final ParameterizedTypeReference<List<InstitusjonsoppholdResponse>> RESPONSE_TYPE_DELETE = new ParameterizedTypeReference<>() {
    };

    private static final String HEADER_NAV_CALL_ID = "Nav-Call-Id";
    private static final String HEADER_NAV_CONSUMER_ID = "Nav-Consumer-Id";
    private static final String NAV_CALL_ID = "orkestratoren";
    private static final String NAV_CONSUMER_ID = "orkestratoren";
    private static final String MILJOE = "q2";

    private RestTemplate restTemplate;
    private UriTemplate startSyntetiseringUrl;
    private UriTemplate sletteIdenterUrl;

    public TestnorgeInstConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${testnorge-inst.rest.api.url}") String instServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.startSyntetiseringUrl = new UriTemplate(instServerUrl + "/v1/syntetisering/generer?miljoe={miljoe}");
        this.sletteIdenterUrl = new UriTemplate(instServerUrl + "/v1/ident/batch?miljoe={miljoe}&identer={identer}");
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "inst" })
    public Object startSyntetisering(
            SyntetiserInstRequest syntetiserInstRequest
    ) {
        var postRequest = RequestEntity.post(startSyntetiseringUrl.expand(MILJOE))
                .contentType(MediaType.APPLICATION_JSON)
                .header(HEADER_NAV_CALL_ID, NAV_CALL_ID)
                .header(HEADER_NAV_CONSUMER_ID, NAV_CONSUMER_ID)
                .body(syntetiserInstRequest);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE_START_SYNT);
    }

    @Timed(value = "orkestratoren.resource.latency", extraTags = { "operation", "inst" })
    public SletteInstitusjonsoppholdResponse slettIdenterFraInst(
            List<String> identer
    ) {
        List<InstitusjonsoppholdResponse> response = new ArrayList<>();

        for (var partisjonerteIdenter : Lists.partition(identer, 80)) {
            var deleteRequest = RequestEntity.delete(sletteIdenterUrl.expand(MILJOE, convertListToString(partisjonerteIdenter)))
                    .header(HEADER_NAV_CALL_ID, NAV_CALL_ID)
                    .header(HEADER_NAV_CONSUMER_ID, NAV_CONSUMER_ID)
                    .build();
            try {
                var body = restTemplate.exchange(deleteRequest, RESPONSE_TYPE_DELETE).getBody();
                if (body != null) {
                    response.addAll(body);
                }
            } catch (HttpStatusCodeException e) {
                log.error("Kunne ikke slette identer fra inst", e);
            }
        }

        return new SletteInstitusjonsoppholdResponse(response);
    }

    private String convertListToString(List<String> list) {
        return String.join(",", list);
    }
}
