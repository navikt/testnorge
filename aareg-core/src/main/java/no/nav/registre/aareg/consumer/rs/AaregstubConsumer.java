package no.nav.registre.aareg.consumer.rs;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.ArrayList;
import java.util.List;

import no.nav.registre.aareg.consumer.rs.responses.ArbeidsforholdsResponse;
import no.nav.registre.aareg.consumer.rs.responses.StatusFraAaregstubResponse;
import no.nav.registre.aareg.provider.rs.responses.SletteArbeidsforholdResponse;

@Component
@Slf4j
public class AaregstubConsumer {

    private static final ParameterizedTypeReference<StatusFraAaregstubResponse> RESPONSE_TYPE_LAGRE = new ParameterizedTypeReference<StatusFraAaregstubResponse>() {
    };

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE_HENT_ARBEIDSTAKERE = new ParameterizedTypeReference<List<String>>() {
    };

    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE_SLETT = new ParameterizedTypeReference<List<Long>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate sendTilAaregstubUrl;
    private UriTemplate hentAlleArbeidstakereUrl;
    private UriTemplate slettIdentUrl;

    public AaregstubConsumer(@Value("${aaregstub.rest.api.url}") String aaregstubServerUrl) {
        this.sendTilAaregstubUrl = new UriTemplate(aaregstubServerUrl + "/v1/lagreArbeidsforhold?lagreIAareg={lagreIAareg}");
        this.hentAlleArbeidstakereUrl = new UriTemplate(aaregstubServerUrl + "/v1/hentAlleArbeidstakere");
        this.slettIdentUrl = new UriTemplate(aaregstubServerUrl + "/v1/slettIdent/{ident}");
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
    public SletteArbeidsforholdResponse slettIdenterFraAaregstub(List<String> identer) {
        SletteArbeidsforholdResponse sletteArbeidsforholdResponse = SletteArbeidsforholdResponse.builder().build();
        for (String ident : identer) {
            RequestEntity deleteRequest = RequestEntity.delete(slettIdentUrl.expand(ident)).build();
            try {
                sletteArbeidsforholdResponse.getIdentermedArbeidsforholdIdSomBleSlettet().put(ident, restTemplate.exchange(deleteRequest, RESPONSE_TYPE_SLETT).getBody());
            } catch (HttpStatusCodeException e) {
                sletteArbeidsforholdResponse.getIdenterSomIkkeKunneSlettes().add(ident);
                log.error("Kunne ikke slette ident {} fra aaregstub", ident, e);
            }
        }
        return sletteArbeidsforholdResponse;
    }
}
