package no.nav.registre.hodejegeren.consumer;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.hodejegeren.consumer.requests.HentIdenterRequest;

@Component
public class IdentPoolConsumer {
    
    @Value("${ident-pool.rest-api.url}")
    private String serverUrl;
    private RestTemplate restTemplate = new RestTemplate();
    
    public List<String> hentNyeIdenter(HentIdenterRequest hentIdenterRequest) {
        return restTemplate.postForObject(serverUrl + "/v1/identifikator", hentIdenterRequest, ArrayList.class);
    }
}
