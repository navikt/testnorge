package no.nav.registre.aareg.consumer.rs;

import static no.nav.registre.aareg.domain.CommonKeys.RESPONSE_TYPE_LIST_AAREG_REQUEST;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.aareg.syntetisering.RsAaregSyntetiseringsRequest;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;

@Component
@Slf4j
@DependencyOn"syntrest")
public class AaregSyntetisererenConsumer {

    @Value("${aareg.pageSize}")
    private int pageSize;

    private final RestTemplate restTemplate;

    private final UriTemplate url;

    public AaregSyntetisererenConsumer(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${syntrest.rest.api.url}") String syntrestServerUrl
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.url = new UriTemplate(syntrestServerUrl + "/v1/generate/aareg");
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "aareg-syntetisereren" })
    public List<RsAaregSyntetiseringsRequest> getSyntetiserteArbeidsforholdsmeldinger(List<String> identer) {
        List<RsAaregSyntetiseringsRequest> syntetiserteMeldinger = new ArrayList<>();
        RequestEntity<List<String>> postRequest;

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

    private void insertSyntetiskeArbeidsforhold(
            List<RsAaregSyntetiseringsRequest> syntetiserteMeldinger,
            RequestEntity<List<String>> postRequest
    ) {
        List<RsAaregSyntetiseringsRequest> response = null;
        try {
            response = restTemplate.exchange(postRequest, RESPONSE_TYPE_LIST_AAREG_REQUEST).getBody();
        } catch (Exception e) {
            log.error("Feil under syntetisering", e);
        }
        if (response != null) {
            syntetiserteMeldinger.addAll(response);
        } else {
            log.error("Kunne ikke hente response body fra synthdata-aareg: NullPointerException");
        }
    }
}
