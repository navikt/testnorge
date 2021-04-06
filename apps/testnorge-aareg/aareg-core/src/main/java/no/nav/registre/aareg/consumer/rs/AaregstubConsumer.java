package no.nav.registre.aareg.consumer.rs;

import static no.nav.registre.aareg.domain.CommonKeys.RESPONSE_TYPE_LIST_LONG;
import static no.nav.registre.aareg.domain.CommonKeys.RESPONSE_TYPE_LIST_STRING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import no.nav.registre.aareg.consumer.ws.request.RsAaregOpprettRequest;
import no.nav.registre.aareg.exception.ResponseNullPointerException;
import no.nav.registre.aareg.provider.rs.response.SletteArbeidsforholdResponse;

@Component
@Slf4j
public class AaregstubConsumer {

    private final RestTemplate restTemplate;

    private final UriTemplate sendTilAaregstubUrl;
    private final UriTemplate hentAlleArbeidstakereUrl;
    private final UriTemplate slettIdentUrl;

    public AaregstubConsumer(
            @Value("${aaregstub.rest.api.url}") String aaregstubServerUrl,
            RestTemplateBuilder restTemplateBuilder
    ) {
        this.sendTilAaregstubUrl = new UriTemplate(aaregstubServerUrl + "/v1/lagreArbeidsforhold");
        this.hentAlleArbeidstakereUrl = new UriTemplate(aaregstubServerUrl + "/v1/hentAlleArbeidstakere");
        this.slettIdentUrl = new UriTemplate(aaregstubServerUrl + "/v1/slettIdent/{ident}");
        this.restTemplate = restTemplateBuilder.build();
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "aaregstub" })
    public List<String> hentEksisterendeIdenter() {
        var getRequest = RequestEntity.get(hentAlleArbeidstakereUrl.expand()).build();
        List<String> eksisterendeIdenter = new ArrayList<>();
        var response = restTemplate.exchange(getRequest, RESPONSE_TYPE_LIST_STRING).getBody();

        if (response != null) {
            eksisterendeIdenter.addAll(response);
        } else {
            log.error("AaregstubConsumer.hentEksisterendeIdenter: Kunne ikke hente response body fra Aaregstub: NullPointerException");
            throw new ResponseNullPointerException("Kunne ikke hente eksisterende ident fra aaregstub");
        }

        return eksisterendeIdenter;
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "aaregstub" })
    public List<String> sendTilAaregstub(
            List<RsAaregOpprettRequest> syntetiserteArbeidsforhold
    ) {
        var postRequest = RequestEntity.post(sendTilAaregstubUrl.expand()).body(syntetiserteArbeidsforhold);

        ResponseEntity<List<String>> response = null;
        try {
            response = restTemplate.exchange(postRequest, RESPONSE_TYPE_LIST_STRING);
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke legge arbeidsforhold inn i aaregstub", e);
        }

        if (response != null && response.getBody() != null) {
            return response.getBody();
        } else {
            log.error("AaregstubConsumer.sendTilAaregstub: Kunne ikke hente response body fra Aaregstub: NullPointerException");
            throw new ResponseNullPointerException("Kunne ikke lagre i aaregstub");
        }
    }

    @Timed(value = "aareg.resource.latency", extraTags = { "operation", "aaregstub" })
    public SletteArbeidsforholdResponse slettIdenterFraAaregstub(
            List<String> identer
    ) {
        var sletteArbeidsforholdResponse = SletteArbeidsforholdResponse.builder()
                .identermedArbeidsforholdIdSomBleSlettet(new HashMap<>())
                .identerSomIkkeKunneSlettes(new ArrayList<>())
                .build();
        for (var ident : identer) {
            var deleteRequest = RequestEntity.delete(slettIdentUrl.expand(ident)).build();
            try {
                sletteArbeidsforholdResponse.getIdentermedArbeidsforholdIdSomBleSlettet().put(ident, restTemplate.exchange(deleteRequest, RESPONSE_TYPE_LIST_LONG).getBody());
            } catch (HttpStatusCodeException e) {
                sletteArbeidsforholdResponse.getIdenterSomIkkeKunneSlettes().add(ident);
                log.error("Kunne ikke slette ident {} fra aaregstub", ident, e);
            }
        }
        return sletteArbeidsforholdResponse;
    }
}
