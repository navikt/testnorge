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
import no.nav.registre.inst.service.Inst2FasitService;

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

    @Autowired
    private Inst2FasitService inst2FasitService;

    private UriTemplate tokenProviderUrl;
    private String username;
    private String password;

    public Inst2Consumer(
            @Value("${freg-token-provider-v1.url}") String tokenProviderServerUrl,
            @Value("${testnorges.ida.credential.inst.username}") String username,
            @Value("${testnorges.ida.credential.inst.password}") String password) {
        this.tokenProviderUrl = new UriTemplate(tokenProviderServerUrl);
        this.username = username;
        this.password = password;
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

    public List<Institusjonsopphold> hentInstitusjonsoppholdFraInst2(Map<String, Object> tokenObject, String callId, String consumerId, String miljoe, String ident) {
        UriTemplate url = new UriTemplate(inst2FasitService.getUrlForEnv(miljoe) + "/person/institusjonsopphold");

        RequestEntity getRequest = RequestEntity.get(url.expand())
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

    public OppholdResponse leggTilInstitusjonsoppholdIInst2(Map<String, Object> tokenObject, String callId, String consumerId, String miljoe, Institusjonsopphold institusjonsopphold) {
        UriTemplate url = new UriTemplate(inst2FasitService.getUrlForEnv(miljoe) + "/person/institusjonsopphold?validatePeriod=true");
        RequestEntity postRequest = RequestEntity.post(url.expand())
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
            log.error("Kunne ikke legge til institusjonsopphold i inst2 på ident - {}", e.getResponseBodyAsString(), e);
            return OppholdResponse.builder().status(e.getStatusCode()).feilmelding(e.getResponseBodyAsString()).build();
        }
    }

    public ResponseEntity oppdaterInstitusjonsoppholdIInst2(Map<String, Object> tokenObject, String callId, String consumerId, String miljoe, Long oppholdId, Institusjonsopphold institusjonsopphold) {
        UriTemplate url = new UriTemplate(inst2FasitService.getUrlForEnv(miljoe) + "/person/institusjonsopphold/{oppholdId}");
        RequestEntity putRequest = RequestEntity.put(url.expand(oppholdId))
                .header("accept", "*/*")
                .header("Authorization", tokenObject.get("tokenType") + " " + tokenObject.get("idToken"))
                .header("Nav-Call-Id", callId)
                .header("Nav-Consumer-Id", consumerId)
                .body(institusjonsopphold);
        try {
            ResponseEntity<Object> response = restTemplate.exchange(putRequest, RESPONSE_TYPE_OBJECT);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke oppdatere institusjonsopphold - {}", e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public ResponseEntity slettInstitusjonsoppholdFraInst2(Map<String, Object> tokenObject, String callId, String consumerId, String miljoe, Long oppholdId) {
        UriTemplate url = new UriTemplate(inst2FasitService.getUrlForEnv(miljoe) + "/person/institusjonsopphold/{oppholdId}");
        RequestEntity deleteRequest = RequestEntity.delete(url.expand(oppholdId))
                .header("accept", "*/*")
                .header("Authorization", tokenObject.get("tokenType") + " " + tokenObject.get("idToken"))
                .header("Nav-Call-Id", callId)
                .header("Nav-Consumer-Id", consumerId)
                .build();
        try {
            ResponseEntity<Object> response = restTemplate.exchange(deleteRequest, RESPONSE_TYPE_OBJECT);
            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke slette institusjonsopphold - {}", e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
        }
    }

    public HttpStatus finnesInstitusjonPaaDato(Map<String, Object> tokenObject, String callId, String consumerId, String miljoe, String tssEksternId, String date) {
        UriTemplate url = new UriTemplate(inst2FasitService.getUrlForEnv(miljoe) + "/institusjon/oppslag/tssEksternId/{tssEksternId}?date={date}");
        RequestEntity getRequest = RequestEntity.get(url.expand(tssEksternId, date))
                .header("accept", "*/*")
                .header("Authorization", tokenObject.get("tokenType") + " " + tokenObject.get("idToken"))
                .header("Nav-Call-Id", callId)
                .header("Nav-Consumer-Id", consumerId)
                .build();

        try {
            ResponseEntity<Object> response = restTemplate.exchange(getRequest, RESPONSE_TYPE_OBJECT);
            return response.getStatusCode();
        } catch (HttpStatusCodeException e) {
            log.warn("Institusjon med tssEksternId {} er ikke gyldig på dato {}.", tssEksternId, date);
            return e.getStatusCode();
        }
    }
}
