package no.nav.registre.aaregstub.arbeidsforhold.consumer.rs;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
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

import java.util.Map;

import no.nav.registre.aaregstub.arbeidsforhold.ArbeidsforholdsResponse;
import no.nav.registre.aaregstub.arbeidsforhold.consumer.rs.responses.DollyResponse;

@Component
@Slf4j
public class DollyConsumer {

    private static final ParameterizedTypeReference<Map<String, Object>> RESPONSE_TYPE_HENT_TOKEN = new ParameterizedTypeReference<Map<String, Object>>() {
    };

    private static final ParameterizedTypeReference<Object> RESPONSE_TYPE = new ParameterizedTypeReference<Object>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate tokenProviderUrl;
    private String username;
    private String password;
    private UriTemplate aaregcontrollerIDollyUrl;

    public DollyConsumer(
            @Value("${freg.token.provider.url}") String tokenProviderServerUrl,
            @Value("${testnorges.ida.credential.dolly.username}") String username,
            @Value("${testnorges.ida.credential.dolly.password}") String password,
            @Value("${dolly.rest.api.url}") String dollyServerUrl) {
        this.tokenProviderUrl = new UriTemplate(tokenProviderServerUrl);
        this.username = username;
        this.password = password;
        this.aaregcontrollerIDollyUrl = new UriTemplate(dollyServerUrl + "/v1/aareg/arbeidsforhold");
    }

    public Map<String, Object> hentTokenTilDolly() {
        RequestEntity getRequest = RequestEntity.get(tokenProviderUrl.expand())
                .header("accept", "*/*")
                .header("username", username)
                .header("password", password)
                .build();
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(getRequest, RESPONSE_TYPE_HENT_TOKEN);

        return response.getBody();
    }

    @Timed(value = "aaregstub.resource.latency", extraTags = { "operation", "dolly" })
    public DollyResponse sendArbeidsforholdTilAareg(Map<String, Object> tokenObject, ArbeidsforholdsResponse syntetisertArbeidsforhold) {
        RequestEntity postRequest = RequestEntity.post(aaregcontrollerIDollyUrl.expand())
                .header(AUTHORIZATION, tokenObject.get("tokenType") + " " + tokenObject.get("idToken"))
                .body(syntetisertArbeidsforhold);

        ResponseEntity<Object> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);

        if (HttpStatus.CREATED.equals(response.getStatusCode()) && response.getBody() != null) {
            DollyResponse dollyResponse = new ObjectMapper().convertValue(response.getBody(), DollyResponse.class);
            dollyResponse.setHttpStatus(response.getStatusCode());
            return dollyResponse;
        } else {
            return DollyResponse.builder().httpStatus(response.getStatusCode()).build();
        }
    }

    @Timed(value = "aaregstub.resource.latency", extraTags = { "operation", "dolly" })
    public ResponseEntity<Object> hentArbeidsforholdFraAareg(Map<String, Object> tokenObject, String fnr, String miljoe) {
        UriTemplate hentArbeidsforholdUrl = new UriTemplate(aaregcontrollerIDollyUrl.toString() + "?ident={fnr}&environment={miljoe}");
        RequestEntity getRequest = RequestEntity.get(hentArbeidsforholdUrl.expand(fnr, miljoe))
                .header(AUTHORIZATION, tokenObject.get("tokenType") + " " + tokenObject.get("idToken"))
                .build();
        ResponseEntity<Object> response;
        try {
            response = restTemplate.exchange(getRequest, RESPONSE_TYPE);
        } catch (HttpStatusCodeException e) {
            response = ResponseEntity.status(e.getStatusCode()).body(e.getResponseBodyAsString());
            log.warn("Kunne ikke finne arbeidsforhold på ident {} i miljø {} i aareg", fnr, miljoe, e);
        }
        return response;
    }
}
