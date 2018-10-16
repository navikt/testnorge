package no.nav.registre.hodejegeren.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;

@Component
public class TpsSyntetisererenConsumer {
    
    @Value("tpssyntetisereren.url")
    private String serverUrl;
    @Value("tpssyntetisereren.credential.username")
    private String username;
    @Value("tpssyntetisereren.credential.password")
    private String password;
    
    private RestTemplate restTemplate;
    
    public TpsSyntetisererenConsumer() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        this.restTemplate = restTemplate;
    }
    
    public List<RsMeldingstype> getSyntetiserteSkdmeldinger(String aarsakskode, Integer antallMeldinger) {
        return restTemplate.getForObject(serverUrl + "", new ArrayList<RsMeldingstype>().getClass(), antallMeldinger, aarsakskode);
    }
}
