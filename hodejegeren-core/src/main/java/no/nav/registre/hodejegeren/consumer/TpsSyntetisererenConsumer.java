package no.nav.registre.hodejegeren.consumer;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;

@Component
public class TpsSyntetisererenConsumer {
    
    private static final ParameterizedTypeReference<List<RsMeldingstype>> RESPONSE_TYPE = new ParameterizedTypeReference<List<RsMeldingstype>>() {
    };
    
    @Value("${tps-syntetisereren.rest-api.url}")
    private String serverUrl;
    
    private RestTemplate restTemplate = new RestTemplate();
    
    public List<RsMeldingstype> getSyntetiserteSkdmeldinger(String aarsakskode, Integer antallMeldinger) {
        URI url = new UriTemplate(serverUrl + "/generate").expand(aarsakskode, antallMeldinger);
        RequestEntity getRequest = RequestEntity.get(url).build();
        return restTemplate.exchange(getRequest, RESPONSE_TYPE).getBody();
    }
}
