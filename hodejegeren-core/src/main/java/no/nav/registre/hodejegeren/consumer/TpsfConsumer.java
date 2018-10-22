package no.nav.registre.hodejegeren.consumer;

import java.util.Arrays;
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
    
    @Value("${tpsf.url}")
    private String serverUrl;
    
    private RestTemplate restTemplate;
    
    public TpsfConsumer(
            @Value("${tpsf.credential.username}") String username,
            @Value("${tpsf.credential.password}") String password
    ) {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        this.restTemplate = restTemplate;
    }
    
    public Set<String> getIdenterFiltrertPaaAarsakskode(Long gruppeId, List<String> aarsakskode, String transaksjonstype) {
        return restTemplate.getForObject(serverUrl + BASE_URL_SKDMELDINGER + "identer/" + gruppeId, Set.class, aarsakskode, transaksjonstype);
    }
    
    public List<Long> saveSkdEndringsmeldingerInTPSF(Long gruppeId, List<RsMeldingstype> skdmeldinger) {
        return Arrays.asList(restTemplate.postForObject(serverUrl + BASE_URL_SKDMELDINGER + "save/" + gruppeId, skdmeldinger, Long[].class));
    }
    
    public String getInfoFromTpsServiceRoutine(String routineName, Map<String, Object> tpsRequestParameters) {
        return restTemplate.getForObject(serverUrl + BASE_URL_SERVICE_ROUTINE + routineName, String.class, tpsRequestParameters);
    }
}
