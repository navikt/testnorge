package no.nav.registre.hodejegeren.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class TpsfConsumer {

    private static final ParameterizedTypeReference<Set<String>> RESPONSE_TYPE_SET = new ParameterizedTypeReference<Set<String>>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate urlGetIdenter;
    private UriTemplate urlServiceRoutine;

    public TpsfConsumer(RestTemplateBuilder restTemplateBuilder,
            @Value("${tps-forvalteren.rest-api.url}") String serverUrl,
            @Value("${testnorges.ida.credential.tpsf.username}") String username,
            @Value("${testnorges.ida.credential.tpsf.password}") String password
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        this.urlGetIdenter = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/identer/{avspillergruppeId}?aarsakskode={aarsakskode}&transaksjonstype={transaksjonstype}");
        this.urlServiceRoutine = new UriTemplate(serverUrl + "/v1/serviceroutine/{routineName}?aksjonsKode={aksjonskode}&environment={environment}&fnr={fnr}");
    }

    @Timed(value = "hodejegeren.resource.latency", extraTags = { "operation", "tpsf" })
    public Set<String> getIdenterFiltrertPaaAarsakskode(Long avspillergruppeId, List<String> aarsakskode, String transaksjonstype) {
        RequestEntity getRequest = RequestEntity.get(urlGetIdenter.expand(avspillergruppeId, StringUtils.join(aarsakskode, ','), transaksjonstype)).build();
        return restTemplate.exchange(getRequest, RESPONSE_TYPE_SET).getBody();
    }

    @Timed(value = "hodejegeren.resource.latency", extraTags = { "operation", "tpsf" })
    public JsonNode getTpsServiceRoutine(String routineName, String aksjonsKode, String environment, String fnr) throws IOException {
        RequestEntity getRequest = RequestEntity.get(urlServiceRoutine.expand(routineName, aksjonsKode, environment, fnr)).build();
        ResponseEntity<String> response = restTemplate.exchange(getRequest, String.class);
        return new ObjectMapper().readTree(response.getBody());
    }
}
