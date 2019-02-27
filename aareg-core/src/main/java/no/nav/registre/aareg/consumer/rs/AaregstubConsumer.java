package no.nav.registre.aareg.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class AaregstubConsumer {

    @Autowired
    private RestTemplate restTemplate;

    private String sendTilAaregstubUrl;

    public AaregstubConsumer(@Value("${testnorge-aaregstub.rest-api.url}") String aaregstubServerUrl) {
        this.sendTilAaregstubUrl = aaregstubServerUrl + "/v1/lagreArbeidsforhold";
    }

    public ResponseEntity sendTilAaregstub(Map<String, List<Map<String, String>>> syntetiserteArbeidsforholdsmeldinger) {
        return restTemplate.postForEntity(sendTilAaregstubUrl, syntetiserteArbeidsforholdsmeldinger, ResponseEntity.class);
    }
}
