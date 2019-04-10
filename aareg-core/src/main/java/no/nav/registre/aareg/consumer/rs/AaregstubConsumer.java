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

import no.nav.registre.aareg.consumer.rs.responses.ArbeidsforholdsResponse;
import no.nav.registre.aareg.consumer.rs.responses.StatusFraAaregstubResponse;

@Component
@Slf4j
public class AaregstubConsumer {

    private static final ParameterizedTypeReference<StatusFraAaregstubResponse> RESPONSE_TYPE_LAGRE = new ParameterizedTypeReference<StatusFraAaregstubResponse>() {
    };

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE_HENT_ARBEIDSTAKERE = new ParameterizedTypeReference<List<String>>() {
    };

    private static final ParameterizedTypeReference<List<ResponseEntity>> RESPONSE_TYPE_SEND_TIL_AAREG = new ParameterizedTypeReference<List<ResponseEntity>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate sendTilAaregstubUrl;
    private UriTemplate hentAlleArbeidstakereUrl;
    private UriTemplate sendTilAaregUrl;

    public AaregstubConsumer(@Value("${testnorge-aaregstub.rest-api.url}") String aaregstubServerUrl) {
        this.sendTilAaregstubUrl = new UriTemplate(aaregstubServerUrl + "/v1/lagreArbeidsforhold?lagreIAareg={lagreIAareg}");
        this.hentAlleArbeidstakereUrl = new UriTemplate(aaregstubServerUrl + "/v1/hentAlleArbeidstakere");
        this.sendTilAaregUrl = new UriTemplate(aaregstubServerUrl + "/v1/sendArbeidsforholdTilAareg");
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
    public StatusFraAaregstubResponse sendTilAaregstub(List<ArbeidsforholdsResponse> syntetiserteArbeidsforhold, Boolean lagreIAareg) {
        RequestEntity postRequest = RequestEntity.post(sendTilAaregstubUrl.expand(lagreIAareg)).body(syntetiserteArbeidsforhold);
        ResponseEntity<StatusFraAaregstubResponse> response = restTemplate.exchange(postRequest, RESPONSE_TYPE_LAGRE);

        if (response.getBody() != null) {
            return response.getBody();
        } else {
            log.error("AaregstubConsumer.sendTilAaregstub: Kunne ikke hente response body fra Aaregstub: NullPointerException");
        }

        return StatusFraAaregstubResponse.builder().build();
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "aaregstub" })
    public List<ResponseEntity> sendArbeidsforholdTilAareg(List<ArbeidsforholdsResponse> syntetiserteArbeidsforhold) {
        RequestEntity postRequest = RequestEntity.post(sendTilAaregUrl.expand()).body(syntetiserteArbeidsforhold);
        List<ResponseEntity> responses = new ArrayList<>();
        ResponseEntity<List<ResponseEntity>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE_SEND_TIL_AAREG);

        if (response.getBody() != null) {
            responses.addAll(response.getBody());
        } else {
            log.error("AaregstubConsumer.sendArbeidsforholdTilAareg: Kunne ikke hente response body fra Aaregstub: NullPointerException");
        }

        return responses;
    }
}
