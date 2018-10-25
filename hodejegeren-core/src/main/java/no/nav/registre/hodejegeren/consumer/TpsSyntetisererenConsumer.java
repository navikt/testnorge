package no.nav.registre.hodejegeren.consumer;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;

@Component
public class TpsSyntetisererenConsumer {
    
    @Value("${tps-syntetisereren.rest-api.url}")
    private String serverUrl;
    
    private RestTemplate restTemplate = new RestTemplate();
    
    public List<RsMeldingstype> getSyntetiserteSkdmeldinger(String aarsakskode, Integer antallMeldinger) {
        return restTemplate.getForObject(serverUrl + "generate", ArrayList.class, antallMeldinger, aarsakskode);
    }
}
