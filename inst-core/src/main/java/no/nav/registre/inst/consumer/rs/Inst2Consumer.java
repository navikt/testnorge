package no.nav.registre.inst.consumer.rs;

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

import no.nav.registre.inst.institusjonsforhold.Institusjonsforholdsmelding;

@Component
@Slf4j
public class Inst2Consumer {

    private static final ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE_HENT_TOKEN = new ParameterizedTypeReference<Map<String, Object>>() {
    };
    private static final ParameterizedTypeReference<List<Institusjonsforholdsmelding>> RESPONSE_TYPE_HENT_INSTITUSJONSOPPHOLD = new ParameterizedTypeReference<List<Institusjonsforholdsmelding>>() {
    };
    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE_LEGG_TIL_INSTITUSJONSOPPHOLD = new ParameterizedTypeReference<Object>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate tokenProviderUrl;
    private String username;
    private String password;
    private UriTemplate hentInstitusjonsoppholdUrl;
    private UriTemplate leggTilInstitusjonsforholdUrl;

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

    public List<Institusjonsforholdsmelding> hentInstitusjonsoppholdFraInst2(Map<String, Object> tokenObject, String ident) {
        RequestEntity getRequest = RequestEntity.get(hentInstitusjonsoppholdUrl.expand())
                .header("accept", "*/*")
                .header("Authorization", tokenObject.get("tokenType") + " " + tokenObject.get("idToken"))
                .header("Nav-Call-Id", "orkestratoren")
                .header("Nav-Consumer-Id", "orkestratoren")
                .header("Nav-Personident", ident)
                .build();
        List<Institusjonsforholdsmelding> response = null;
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

    public ResponseEntity leggTilInstitusjonsoppholdIInst2(Map<String, Object> tokenObject, Institusjonsforholdsmelding institusjonsforholdsmelding) {
        RequestEntity postRequest = RequestEntity.post(leggTilInstitusjonsforholdUrl.expand())
                .header("accept", "*/*")
                .header("Authorization", tokenObject.get("tokenType") + " " + tokenObject.get("idToken"))
                .header("Nav-Call-Id", "orkestratoren")
                .header("Nav-Consumer-Id", "orkestratoren")
                .body(institusjonsforholdsmelding);
        try {
            return restTemplate.exchange(postRequest, RESPONSE_TYPE_LEGG_TIL_INSTITUSJONSOPPHOLD);
        } catch (HttpStatusCodeException e) {
            log.error("Kunne ikke legge til institusjonsopphold i inst2 - {}", e.getResponseBodyAsString(), e);
            return ResponseEntity.status(e.getStatusCode()).headers(e.getResponseHeaders()).body(e.getResponseBodyAsString());
        }
    }
}
