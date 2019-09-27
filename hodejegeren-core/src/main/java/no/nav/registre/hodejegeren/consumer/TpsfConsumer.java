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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Component
@Slf4j
public class TpsfConsumer {

    private static final ParameterizedTypeReference<Set<String>> RESPONSE_TYPE_SET = new ParameterizedTypeReference<Set<String>>() {
    };
    private static final ParameterizedTypeReference<List<Long>> RESPONSE_TYPE_LIST = new ParameterizedTypeReference<List<Long>>() {
    };

    private RestTemplate restTemplate;
    private UriTemplate urlGetIdenter;
    private UriTemplate urlServiceRoutine;
    private UriTemplate statusPaaIdenter;
    private UriTemplate urlGetMeldingIder;

    public TpsfConsumer(RestTemplateBuilder restTemplateBuilder,
            @Value("${tps-forvalteren.rest-api.url}") String serverUrl,
            @Value("${testnorges.ida.credential.tpsf.username}") String username,
            @Value("${testnorges.ida.credential.tpsf.password}") String password
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        this.urlGetIdenter = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/identer/{avspillergruppeId}?aarsakskode={aarsakskode}&transaksjonstype={transaksjonstype}");
        this.urlServiceRoutine = new UriTemplate(serverUrl + "/v1/serviceroutine/{routineName}?aksjonsKode={aksjonskode}&environment={miljoe}&fnr={fnr}");
        this.statusPaaIdenter = new UriTemplate(serverUrl + "/v1/serviceroutine/FS03-FDLISTER-DISKNAVN-M?aksjonsKode={aksjonskode}&antallFnr={antallIdenter}&environment={miljoe}&nFnr={identer}");
        this.urlGetMeldingIder = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/meldinger/{avspillergruppeId}");
    }

    @Timed(value = "hodejegeren.resource.latency", extraTags = { "operation", "tpsf" })
    public Set<String> getIdenterFiltrertPaaAarsakskode(Long avspillergruppeId, List<String> aarsakskode, String transaksjonstype) {
        RequestEntity getRequest = RequestEntity.get(urlGetIdenter.expand(avspillergruppeId, StringUtils.join(aarsakskode, ','), transaksjonstype)).build();
        return restTemplate.exchange(getRequest, RESPONSE_TYPE_SET).getBody();
    }

    @Timed(value = "hodejegeren.resource.latency", extraTags = { "operation", "tpsf" })
    public JsonNode getTpsServiceRoutine(String routineName, String aksjonsKode, String miljoe, String fnr) throws IOException {
        RequestEntity getRequest = RequestEntity.get(urlServiceRoutine.expand(routineName, aksjonsKode, miljoe, fnr)).build();
        ResponseEntity<String> response = restTemplate.exchange(getRequest, String.class);
        return new ObjectMapper().readTree(response.getBody());
    }

    @Timed(value = "hodejegeren.resource.latency", extraTags = { "operation", "tpsf" })
    public JsonNode hentTpsStatusPaaIdenter(String aksjonskode, String miljoe, List<String> identer) throws IOException {
        String identerSomString = String.join(",", identer);
        RequestEntity getRequest = RequestEntity.get(statusPaaIdenter.expand(aksjonskode, identer.size(), miljoe, identerSomString)).build();
        ResponseEntity<String> response = restTemplate.exchange(getRequest, String.class);
        return new ObjectMapper().readTree(response.getBody()).findValue("response");
    }

    @Timed(value = "hodejegeren.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> getMeldingIderTilhoerendeIdenter(Long avspillergruppeId, List<String> identer) {
        RequestEntity postRequest = RequestEntity.post(urlGetMeldingIder.expand(avspillergruppeId)).body(identer);
        return new ArrayList<>(Objects.requireNonNull(restTemplate.exchange(postRequest, RESPONSE_TYPE_LIST).getBody()));
    }
}
