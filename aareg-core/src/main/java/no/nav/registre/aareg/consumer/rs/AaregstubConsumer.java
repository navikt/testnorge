package no.nav.registre.aareg.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AaregstubConsumer {

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate sendTilAaregstubUrl;

    public AaregstubConsumer(@Value("{testnorge-aaregstub.rest-api.url}") String aaregstubServerUrl) {
        this.sendTilAaregstubUrl = new UriTemplate(aaregstubServerUrl + "/v1/syntetisering/lagreArbeidsforhold");
    }

    public ResponseEntity sendTilAaregstub(Map<String, List<Map<String, String>>> syntetiserteArbeidsforholdsmeldinger) {
        RequestEntity postRequest = RequestEntity.post(sendTilAaregstubUrl.expand()).body(syntetiserteArbeidsforholdsmeldinger);

        return restTemplate.exchange(postRequest, ResponseEntity.class);
    }
}
