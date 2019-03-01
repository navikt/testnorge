package no.nav.registre.aareg.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AaregstubConsumer {

    private static final ParameterizedTypeReference<ResponseEntity> RESPONSE_TYPE_LAGRE = new ParameterizedTypeReference<ResponseEntity>() {
    };

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE_HENT_ARBEIDSTAKERE = new ParameterizedTypeReference<List<String>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate sendTilAaregstubUrl;
    private UriTemplate hentAlleArbeidstakereUrl;

    public AaregstubConsumer(@Value("${testnorge-aaregstub.rest-api.url}") String aaregstubServerUrl) {
        this.sendTilAaregstubUrl = new UriTemplate(aaregstubServerUrl + "/v1/lagreArbeidsforhold");
        this.hentAlleArbeidstakereUrl = new UriTemplate(aaregstubServerUrl + "/v1/hentAlleArbeidstakere");
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "aaregstub" })
    public List<String> hentEksisterendeIdenter() {
        RequestEntity getRequest = RequestEntity.get(hentAlleArbeidstakereUrl.expand()).build();
        List<String> eksisterendeIdenter = new ArrayList<>();
        ResponseEntity<List<String>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE_HENT_ARBEIDSTAKERE);

        if (response.getBody() != null) {
            eksisterendeIdenter.addAll(response.getBody());
        } else {
            log.error("AaregstubConsumer.hentEksisterendeIdenter: Kunne ikke hente response body fra Aaregstub: NullPointerException");
        }

        return eksisterendeIdenter;
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "aaregstub" })
    public ResponseEntity sendTilAaregstub(Map<String, List<Map<String, String>>> syntetiserteArbeidsforholdsmeldinger) {
        RequestEntity postRequest = RequestEntity.post(sendTilAaregstubUrl.expand()).body(syntetiserteArbeidsforholdsmeldinger);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE_LAGRE);
    }
}
