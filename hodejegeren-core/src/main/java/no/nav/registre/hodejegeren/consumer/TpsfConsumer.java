package no.nav.registre.hodejegeren.consumer;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;

@Component
public class TpsfConsumer {
    
    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<List<Long>>() {
    };
    private static final String BASE_PATH_SKDMELDINGER = "api/v1/endringsmelding/skd/";
    private static final String BASE_URL_SERVICE_ROUTINE = "api/v1/serviceroutine/";
    
    @Value("${tpsf.url}")
    private String serverUrl;
    private RestTemplate restTemplate;
    
    public TpsfConsumer(
            @Value("${hodejegeren.ida.credential.username}") String username,
            @Value("${hodejegeren.ida.credential.password}") String password
    ) {
        this.restTemplate = new RestTemplate();
        this.restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
    }
    
    public Set<String> getIdenterFiltrertPaaAarsakskode(Long gruppeId, List<String> aarsakskode, String transaksjonstype) {
        return restTemplate.getForObject(serverUrl + BASE_PATH_SKDMELDINGER + "identer/" + gruppeId, Set.class, aarsakskode, transaksjonstype);
    }
    
    public List<Long> saveSkdEndringsmeldingerInTPSF(Long gruppeId, List<RsMeldingstype> skdmeldinger) {
        URI url = new UriTemplate(serverUrl + BASE_PATH_SKDMELDINGER + "save/{gruppeId}").expand(gruppeId);
        RequestEntity postRequest = RequestEntity.post(url).body(skdmeldinger);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }

    public JsonNode getTpsServiceRoutine(String routineName, Map<String, Object> tpsRequestParameters) throws IOException {
        String response = restTemplate.getForObject(serverUrl + BASE_PATH_SERVICE_ROUTINE + routineName, String.class, tpsRequestParameters);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readTree(response);
    }
}
