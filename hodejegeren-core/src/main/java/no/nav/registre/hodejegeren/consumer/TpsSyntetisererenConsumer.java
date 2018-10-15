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
    @Value("tpssyntetisereren.login.username")
    private String username;
    @Value("tpssyntetisereren.login.password")
    private String password;
    
    private RestTemplate restTemplate;
    
    public TpsSyntetisererenConsumer() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        this.restTemplate = restTemplate;
    }
    
    public List<RsMeldingstype> getSyntetiserteSkdmeldinger(Map<String, String> antallMeldingerPerAarsakskode) {
        return restTemplate.getForObject(serverUrl + "", new ArrayList<RsMeldingstype>().getClass(), antallMeldingerPerAarsakskode);
    }
}
