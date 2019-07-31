package no.nav.registre.inst.consumer.rs;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import no.nav.registre.inst.Institusjonsopphold;
import no.nav.registre.inst.provider.rs.responses.OppholdResponse;

@Component
@Slf4j
public class Inst2Consumer {

    private static final ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE_HENT_TOKEN = new ParameterizedTypeReference<Map<String, Object>>() {
    };
    private static final ParameterizedTypeReference<List<Institusjonsopphold>> RESPONSE_TYPE_HENT_INSTITUSJONSOPPHOLD = new ParameterizedTypeReference<List<Institusjonsopphold>>() {
    };
    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE_OBJECT = new ParameterizedTypeReference<Object>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate tokenProviderUrl;
    private String username;
    private String password;
    private UriTemplate hentInstitusjonsoppholdUrl;
    private UriTemplate leggTilInstitusjonsforholdUrl;
    private UriTemplate slettInstitusjonsforholdUrl;
    private UriTemplate sjekkInstitusjonUrl;

    public Inst2Consumer(
            @Value("${freg-token-provider-v1.url}") String tokenProviderServerUrl,
            @Value("${testnorges.ida.credential.inst.username}") String username,
            @Value("${testnorges.ida.credential.inst.password}") String password,
            @Value("${inst2.web.api.url}") String inst2ServerUrl) {
        this.tokenProviderUrl = new UriTemplate(tokenProviderServerUrl);
        this.username = username;
        this.password = password;
        this.hentInstitusjonsoppholdUrl = new UriTemplate(inst2ServerUrl + "/person/institusjonsopphold");
        this.leggTilInstitusjonsforholdUrl = new UriTemplate(inst2ServerUrl + "/person/institusjonsopphold?validatePeriod=true");
        this.slettInstitusjonsforholdUrl = new UriTemplate(inst2ServerUrl + "/person/institusjonsopphold/{oppholdId}");
        this.sjekkInstitusjonUrl = new UriTemplate(inst2ServerUrl + "/institusjon/oppslag/tssEksternId/{tssEksternId}?date={date}");
    }

    public Map<String, Object> hentTokenTilInst2() {
        RequestEntity getRequest = RequestEntity.get(tokenProviderUrl.expand())
                .header("accept", "*/*")
                .header("username", username)
                .header("password", password)
                .build();
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE_HENT_TOKEN);

        return response.getBody();
    }

    public List<Institusjonsopphold> hentInstitusjonsoppholdFraInst2(Map<String, Object> tokenObject, String ident, String callId, String consumerId) {
        RequestEntity getRequest = RequestEntity.get(hentInstitusjonsoppholdUrl.expand())
                .header("accept", "*/*")
                .header("Authorization", tokenObject.get("tokenType") + " " + tokenObject.get("idToken"))
                .header("Nav-Call-Id", callId)
                .header("Nav-Consumer-Id", consumerId)
                .header("Nav-Personident", ident)
                .build();
        List<Institusjonsopphold> response = null;
        try {
            response = restTemplate.exchange(getRequest, RESPONSE_TYPE_HENT_INSTITUSJONSOPPHOLD).getBody();
        } catch (HttpStatusCodeException e) {
            assert e.getMessage() != null;
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                return new ArrayList<>();
            } else {
                log.error("Kunne ikke hente ident fra inst2", e);
            }
        }
        return response;
    }

    public OppholdResponse leggTilInstitusjonsoppholdIInst2(Map<String, Object> tokenObject, Institusjonsopphold institusjonsopphold, String callId, String consumerId) {
        RequestEntity postRequest = RequestEntity.post(leggTilInstitusjonsforholdUrl.expand())
                .header("accept", "*/*")
                .header("Authorization", tokenObject.get("tokenType") + " " + tokenObject.get("idToken"))
                .header("Nav-Call-Id", callId)
                .header("Nav-Consumer-Id", consumerId)
                .body(institusjonsopphold);
        try {
            ResponseEntity<Object> exchange = restTemplate.exchange(postRequest, RESPONSE_TYPE_OBJECT);
            Institusjonsopphold institusjonsopphold1 = new ObjectMapper().convertValue(exchange.getBody(), Institusjonsopphold.class);
            return OppholdResponse.builder().status(exchange.getStatusCode()).institusjonsopphold(institusjonsopphold1).build();
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke legge til institusjonsopphold i inst2 på ident {} med tssEksternId {} - {}",
                    institusjonsopphold.getPersonident(), institusjonsopphold.getTssEksternId(), e.getResponseBodyAsString(), e);
            ResponseEntity.status(e.getStatusCode()).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
            return OppholdResponse.builder().status(e.getStatusCode()).feilmelding(e.getResponseBodyAsString()).build();
        }
    }

    public ResponseEntity slettInstitusjonsoppholdFraInst2(Map<String, Object> tokenObject, Long oppholdId, String callId, String consumerId) {
        RequestEntity deleteRequest = RequestEntity.delete(slettInstitusjonsforholdUrl.expand(oppholdId))
                .header("accept", "*/*")
                .header("Authorization", tokenObject.get("tokenType") + " " + tokenObject.get("idToken"))
                .header("Nav-Call-Id", callId)
                .header("Nav-Consumer-Id", consumerId)
                .build();
        try {
            ResponseEntity<Object> response = restTemplate.exchange(deleteRequest, RESPONSE_TYPE_OBJECT);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke slette institusjonsopphold med oppholdId {} - {}", oppholdId, e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public HttpStatus finnesInstitusjonPaaDato(Map<String, Object> tokenObject, String tssEksternId, String date, String callId, String consumerId) {
        RequestEntity getRequest = RequestEntity.get(sjekkInstitusjonUrl.expand(tssEksternId, date))
                .header("accept", "*/*")
                .header("Authorization", tokenObject.get("tokenType") + " " + tokenObject.get("idToken"))
                .header("Nav-Call-Id", callId)
                .header("Nav-Consumer-Id", consumerId)
                .build();

        try {
            ResponseEntity<Object> response = restTemplate.exchange(getRequest, RESPONSE_TYPE_OBJECT);
            return response.getStatusCode();
        } catch (HttpStatusCodeException e) {
            log.warn("Institusjon med tssEksternId {} er ikke gyldig på dato {}", tssEksternId, date, e);
            return e.getStatusCode();
        }
    }
}
