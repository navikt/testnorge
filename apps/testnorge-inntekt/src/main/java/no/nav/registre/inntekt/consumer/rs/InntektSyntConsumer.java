package no.nav.registre.inntekt.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Map;
import java.util.SortedMap;

import no.nav.registre.inntekt.domain.inntektstub.RsInntekt;

@Slf4j
@Component
public class InntektSyntConsumer {

    private static final ParameterizedTypeReference<SortedMap<String, List<RsInntekt>>> RESPONSE_TYPE = new ParameterizedTypeReference<SortedMap<String, List<RsInntekt>>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public InntektSyntConsumer(@Value("${syntrest.rest.api.url}") String inntektSyntUrl) {
        url = new UriTemplate(inntektSyntUrl + "/v1/generate/inntekt");
    }

    public SortedMap<String, List<RsInntekt>> hentSyntetiserteInntektsmeldinger(Map<String, List<RsInntekt>> identerMedInntekt) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).body(identerMedInntekt);
        try {
            return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
        } catch (HttpStatusCodeException e) {
            log.warn(e.getMessage(), e);
        } catch (Exception e) {
            log.error("Uventet feil fra syntetisering", e);
        }
        return null;
    }
}
