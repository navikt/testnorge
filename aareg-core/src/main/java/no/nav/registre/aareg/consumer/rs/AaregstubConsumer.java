package no.nav.registre.aareg.consumer.rs;

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

    private static final ParameterizedTypeReference<List<String>> RESPONSE_TYPE = new ParameterizedTypeReference<List<String>>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate url;

    public AaregstubConsumer(@Value("{testnorge-aaregstub.rest-api.url}") String aaregstubServerUrl) {
        this.url = new UriTemplate(aaregstubServerUrl + "/v1/");
    }

    public List<String> sendTilAaregstub(Map<String, List<Map<String, String>>> syntetiserteArbeidsforholdsmeldinger) {
        RequestEntity postRequest = RequestEntity.post(url.expand()).body(syntetiserteArbeidsforholdsmeldinger);

        List<String> resultat = new ArrayList<>();

        ResponseEntity<List<String>> response = restTemplate.exchange(postRequest, RESPONSE_TYPE);

        if (response != null && response.getBody() != null) {
            resultat.addAll(response.getBody());
        } else {
            log.error("Kunne ikke hente response body fra aaregstub: NullPointerException");
        }

        return resultat;
    }
}
