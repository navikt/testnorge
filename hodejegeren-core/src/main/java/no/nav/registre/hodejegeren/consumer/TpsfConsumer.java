package no.nav.registre.hodejegeren.consumer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;

@Component
public class TpsfConsumer {
    
    private static String BASE_URL_SKDMELDINGER = "api/v1/endringsmelding/skd/";
    private static String BASE_URL_SERVICE_ROUTINE = "api/v1/serviceroutine/";
    
    @Value("tpsf.url")
    private String url;
    @Value("tpsf.login.username")
    private String username;
    @Value("tpsf.login.password")
    private String password;
    
    private RestTemplate restTemplate;
    
    public TpsfConsumer() {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        this.restTemplate = restTemplate;
    }
    
    public Set<String> getIdenterFiltrertPaaAarsakskode(Long gruppeId, List<String> aarsakskode, String transaksjonstype) {
        return restTemplate.getForObject(url + BASE_URL_SKDMELDINGER + "identer/" + gruppeId, Set.class, aarsakskode, transaksjonstype);
    }
    
    public List<Long> saveSkdEndringsmeldingerInTPSF(Long gruppeId, List<RsMeldingstype> skdmeldinger) {
        return restTemplate.postForObject(url + BASE_URL_SKDMELDINGER + "save/" + gruppeId, skdmeldinger, ArrayList.class);
    }
    
    public String getInfoFromTpsServiceRoutine(String routineName, Map<String, Object> tpsRequestParameters) {
        return restTemplate.getForObject(url + BASE_URL_SERVICE_ROUTINE + routineName, String.class, tpsRequestParameters);
    }
}
