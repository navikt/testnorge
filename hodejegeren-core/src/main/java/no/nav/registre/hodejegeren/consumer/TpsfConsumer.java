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

import no.nav.registre.hodejegeren.consumer.requests.SlettSkdmeldingerRequest;

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
    private UriTemplate urlGetMeldingIder;
    private UriTemplate urlSlettMeldinger;

    public TpsfConsumer(RestTemplateBuilder restTemplateBuilder,
            @Value("${tps-forvalteren.rest-api.url}") String serverUrl,
            @Value("${testnorges.ida.credential.tpsf.username}") String username,
            @Value("${testnorges.ida.credential.tpsf.password}") String password
    ) {
        this.restTemplate = restTemplateBuilder.build();
        this.restTemplate.getInterceptors().add(new BasicAuthorizationInterceptor(username, password));
        this.urlGetIdenter = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/identer/{avspillergruppeId}?aarsakskode={aarsakskode}&transaksjonstype={transaksjonstype}");
        this.urlServiceRoutine = new UriTemplate(serverUrl + "/v1/serviceroutine/{routineName}?aksjonsKode={aksjonskode}&environment={environment}&fnr={fnr}");
        this.urlGetMeldingIder = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/meldinger/{avspillergruppeId}");
        this.urlSlettMeldinger = new UriTemplate(serverUrl + "/v1/endringsmelding/skd/deletemeldinger");
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

    @Timed(value = "hodejegeren.resource.latency", extraTags = { "operation", "tpsf" })
    public List<Long> getMeldingIderTilhoerendeIdenter(Long avspillergruppeId, List<String> identer) {
        RequestEntity postRequest = RequestEntity.post(urlGetMeldingIder.expand(avspillergruppeId)).body(identer);
        return new ArrayList<>(Objects.requireNonNull(restTemplate.exchange(postRequest, RESPONSE_TYPE_LIST).getBody()));
    }

    @Timed(value = "hodejegeren.resource.latency", extraTags = { "operation", "tpsf" })
    public ResponseEntity slettMeldingerFraTpsf(List<Long> meldingIder) {
        RequestEntity postRequest = RequestEntity.post(urlSlettMeldinger.expand()).body(SlettSkdmeldingerRequest.builder().ids(meldingIder).build());
        return restTemplate.exchange(postRequest, ResponseEntity.class);
    }
}
