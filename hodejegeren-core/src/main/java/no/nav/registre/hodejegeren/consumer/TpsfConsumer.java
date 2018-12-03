package no.nav.registre.hodejegeren.consumer;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.hodejegeren.skdmelding.RsMeldingstype;

@Component
@Slf4j
public class TpsfConsumer {

    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE = new ParameterizedTypeReference<List<Long>>() {
    };

    private RestTemplate restTemplate;
    private ObjectMapper objectMapper = new ObjectMapper();
    private UriTemplate uriTemplateSave;
    private String urlGetIdenter;
    private String urlServiceRoutine;

    public TpsfConsumer(RestTemplateBuilder restTemplateBuilder,
                        @Value("${tps-forvalteren.rest-api.url}") String serverUrl,
                        @Value("${testnorges.ida.credential.tpsf.username}") String username,
                        @Value("${testnorges.ida.credential.tpsf.password}") String password
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        this.uriTemplateSave = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/save/{gruppeId}");
        this.urlGetIdenter = serverUrl + "/v1/endringsmelding/skd/identer/{gruppeId}?aarsakskode={aarsakskode}&transaksjonstype={transaksjonstype}";
        this.urlServiceRoutine = serverUrl + "/v1/serviceroutine/{routineName}?aksjonsKode={aksjonskode}&environment={environment}&fnr={fnr}";
    }

    public Set<String> getIdenterFiltrertPaaAarsakskode(Long gruppeId, List<String> aarsakskode, String transaksjonstype) {
        return restTemplate.getForObject(urlGetIdenter, Set.class, gruppeId, StringUtils.join(aarsakskode, ','), transaksjonstype);
    }

    public List<Long> saveSkdEndringsmeldingerInTPSF(Long gruppeId, List<RsMeldingstype> skdmeldinger) {
        URI url = uriTemplateSave.expand(gruppeId);
        RequestEntity postRequest = RequestEntity.post(url).body(skdmeldinger);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE).getBody();
    }

    public JsonNode getTpsServiceRoutine(String routineName, String aksjonsKode, String environment, String fnr) throws IOException {
        String response = restTemplate.getForObject(urlServiceRoutine, String.class, routineName, aksjonsKode, environment, fnr);
        if(response == null) {
            log.warn("Respons fra TPS er null for rutine {} på fnr {}", routineName, fnr);
        }
        else if(response.isEmpty()) {
            log.warn("Respons fra TPS er tom for rutine {} på fnr {}", routineName, fnr);
        }
        return objectMapper.readTree(response);
    }
}
