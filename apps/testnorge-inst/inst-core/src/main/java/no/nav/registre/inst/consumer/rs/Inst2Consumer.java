package no.nav.registre.inst.consumer.rs;

import static no.nav.registre.inst.properties.HttpRequestConstants.ACCEPT;
import static no.nav.registre.inst.properties.HttpRequestConstants.AUTHORIZATION;
import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CALL_ID;
import static no.nav.registre.inst.properties.HttpRequestConstants.HEADER_NAV_CONSUMER_ID;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.exception.UgyldigIdentResponseException;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;

@Component
@Slf4j
public class Inst2Consumer {

    private static final ParameterizedTypeReference<List<Institusjonsopphold>> RESPONSE_TYPE_HENT_INSTITUSJONSOPPHOLD = new ParameterizedTypeReference<>() {
    };

    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE_OBJECT = new ParameterizedTypeReference<>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate inst2WebApiServerUrl;
    private UriTemplate inst2ServerUrl;

    public Inst2Consumer(
            @Value("${inst2.web.api.url}") String inst2WebApiServerUrl,
            @Value("${inst2.api.url}") String inst2ServerUrl
    ) {
        this.inst2WebApiServerUrl = new UriTemplate(inst2WebApiServerUrl);
        this.inst2ServerUrl = new UriTemplate(inst2ServerUrl);
    }

    public List<Institusjonsopphold> hentInstitusjonsoppholdFraInst2(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            String ident
    ) {
        var getRequest = RequestEntity.get(new UriTemplate(inst2WebApiServerUrl.expand(miljoe) + "/person/institusjonsopphold").expand())
                .header(ACCEPT, "*/*")
                .header(AUTHORIZATION, bearerToken)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, consumerId)
                .header("Nav-Personident", ident)
                .build();
        List<Institusjonsopphold> response;
        try {
            response = restTemplate.exchange(getRequest, RESPONSE_TYPE_HENT_INSTITUSJONSOPPHOLD).getBody();
        } catch (HttpStatusCodeException e) {
            assert e.getMessage() != null;
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return new ArrayList<>();
            } else {
                log.error("Kunne ikke hente ident fra inst2", e);
                throw new UgyldigIdentResponseException("Kunne ikke hente ident fra inst2", e);
            }
        }
        return response;
    }

    public OppholdResponse leggTilInstitusjonsoppholdIInst2(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            Institusjonsopphold institusjonsopphold
    ) {
        var postRequest = RequestEntity.post(new UriTemplate(inst2WebApiServerUrl.expand(miljoe) + "/person/institusjonsopphold?validatePeriod=true").expand())
                .header(ACCEPT, "*/*")
                .header(AUTHORIZATION, bearerToken)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, consumerId)
                .body(institusjonsopphold);
        try {
            var response = restTemplate.exchange(postRequest, RESPONSE_TYPE_OBJECT);
            Institusjonsopphold institusjonsoppholdResponse = new ObjectMapper().convertValue(response.getBody(), Institusjonsopphold.class);
            return OppholdResponse.builder()
                    .status(response.getStatusCode())
                    .institusjonsopphold(institusjonsoppholdResponse)
                    .build();
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke legge til institusjonsopphold i inst2 på ident - {}", e.getResponseBodyAsString(), e);
            return OppholdResponse.builder()
                    .status(e.getStatusCode())
                    .feilmelding(e.getResponseBodyAsString())
                    .build();
        }
    }

    public ResponseEntity<Object> oppdaterInstitusjonsoppholdIInst2(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            Long oppholdId,
            Institusjonsopphold institusjonsopphold
    ) {
        var putRequest = RequestEntity.put(new UriTemplate(inst2WebApiServerUrl.expand(miljoe) + "/person/institusjonsopphold/{oppholdId}").expand(oppholdId))
                .header(ACCEPT, "*/*")
                .header(AUTHORIZATION, bearerToken)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, consumerId)
                .body(institusjonsopphold);
        try {
            var response = restTemplate.exchange(putRequest, RESPONSE_TYPE_OBJECT);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke oppdatere institusjonsopphold - {}", e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public ResponseEntity<Object> slettInstitusjonsoppholdFraInst2(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            Long oppholdId
    ) {
        var deleteRequest = RequestEntity.delete(new UriTemplate(inst2WebApiServerUrl.expand(miljoe) + "/person/institusjonsopphold/{oppholdId}").expand(oppholdId))
                .header(ACCEPT, "*/*")
                .header(AUTHORIZATION, bearerToken)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, consumerId)
                .build();
        try {
            var response = restTemplate.exchange(deleteRequest, RESPONSE_TYPE_OBJECT);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke slette institusjonsopphold - {}", e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public HttpStatus finnesInstitusjonPaaDato(
            String bearerToken,
            String callId,
            String consumerId,
            String miljoe,
            String tssEksternId,
            LocalDate date
    ) {
        var getRequest = RequestEntity.get(new UriTemplate(inst2WebApiServerUrl.expand(miljoe) + "/institusjon/oppslag/tssEksternId/{tssEksternId}?date={date}")
                .expand(tssEksternId, date))
                .header(ACCEPT, "*/*")
                .header(AUTHORIZATION, bearerToken)
                .header(HEADER_NAV_CALL_ID, callId)
                .header(HEADER_NAV_CONSUMER_ID, consumerId)
                .build();

        try {
            var response = restTemplate.exchange(getRequest, RESPONSE_TYPE_OBJECT);
            return response.getStatusCode();
        } catch (HttpStatusCodeException e) {
            log.debug("Institusjon med tssEksternId {} er ikke gyldig på dato {}.", tssEksternId, date);
            return e.getStatusCode();
        }
    }

    public boolean isMiljoeTilgjengelig(String miljoe) {
        var optionsRequest = RequestEntity.options(inst2ServerUrl.expand(miljoe)).build();
        try {
            restTemplate.exchange(optionsRequest, Void.class);
            return true;
        } catch (HttpStatusCodeException e) {
            log.warn("Inst2 er ikke tilgjengelig i miljø {}", miljoe);
            return false;
        }
    }
}
