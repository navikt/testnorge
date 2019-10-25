package no.nav.registre.aareg.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;

@Component
@Slf4j
public class AaregSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<RsAaregOpprettRequest>> RESPONSE_TYPE = new ParameterizedTypeReference<List<RsAaregOpprettRequest>>() {
    };

    @Value("${aareg.pageSize}")
    private int pageSize;

    private final RestTemplate restTemplate;

    private final UriTemplate url;

    public AaregSyntetisererenConsumer(
            @Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.restTemplate = new RestTemplate();
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generate/aareg");
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "aareg-syntetisereren" })
    public List<RsAaregOpprettRequest> getSyntetiserteArbeidsforholdsmeldinger(List<String> identer) {
        List<RsAaregOpprettRequest> syntetiserteMeldinger = new ArrayList<>();
        RequestEntity postRequest;

        if (identer.size() > pageSize) {
            for (int i = 0; i * pageSize < identer.size(); i++) {
                int endIndex = pageSize * (i + 1);
                if (endIndex > identer.size()) {
                    endIndex = identer.size();
                }

                postRequest = RequestEntity.post(url.expand()).body(identer.subList(i * pageSize, endIndex));

                insertSyntetiskeArbeidsforhold(syntetiserteMeldinger, postRequest);
            }
        } else {
            postRequest = RequestEntity.post(url.expand()).body(identer);

            insertSyntetiskeArbeidsforhold(syntetiserteMeldinger, postRequest);
        }

        return syntetiserteMeldinger;
    }

    private void insertSyntetiskeArbeidsforhold(List<RsAaregOpprettRequest> syntetiserteMeldinger, RequestEntity postRequest) {
        ResponseEntity<List<RsAaregOpprettRequest>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);
        if (response.getBody() != null) {
            syntetiserteMeldinger.addAll(response.getBody());
        } else {
            log.error("Kunne ikke hente response body fra synthdata-aareg: NullPointerException");
        }
    }
}
