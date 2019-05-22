package no.nav.registre.hodejegeren.consumer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.micrometer.core.annotation.Timed;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.buf.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.List;
import java.util.Set;

@Component
@Slf4j
public class TpsfConsumer {

    private RestTemplate restTemplate;
    private String urlGetIdenter;
    private String urlServiceRoutine;

    public TpsfConsumer(RestTemplateBuilder restTemplateBuilder,
            @Value("${tps-forvalteren.rest-api.url}") String serverUrl,
            @Value("${testnorges.ida.credential.tpsf.username}") String username,
            @Value("${testnorges.ida.credential.tpsf.password}") String password
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        this.urlGetIdenter = serverUrl + "/v1/endringsmelding/skd/identer/{gruppeId}?aarsakskode={aarsakskode}&transaksjonstype={transaksjonstype}";
        this.urlServiceRoutine = serverUrl + "/v1/serviceroutine/{routineName}?aksjonsKode={aksjonskode}&environment={environment}&fnr={fnr}";
    }

    @Timed(value = "hodejegeren.resource.latency", extraTags = { "operation", "tpsf" })
    public Set<String> getIdenterFiltrertPaaAarsakskode(Long gruppeId, List<String> aarsakskode, String transaksjonstype) {
        return restTemplate.getForObject(urlGetIdenter, Set.class, gruppeId, StringUtils.join(aarsakskode, ','), transaksjonstype);
    }

    @Timed(value = "hodejegeren.resource.latency", extraTags = { "operation", "tpsf" })
    public JsonNode getTpsServiceRoutine(String routineName, String aksjonsKode, String environment, String fnr) throws IOException {
        String response = restTemplate.getForObject(urlServiceRoutine, String.class, routineName, aksjonsKode, environment, fnr);
        if (response == null) {
            log.warn("Respons fra TPS er null for rutine {} på fnr {}", routineName, fnr);
        } else if (response.isEmpty()) {
            log.warn("Respons fra TPS er tom for rutine {} på fnr {}", routineName, fnr);
        }
        return new ObjectMapper().readTree(response);
    }
}
