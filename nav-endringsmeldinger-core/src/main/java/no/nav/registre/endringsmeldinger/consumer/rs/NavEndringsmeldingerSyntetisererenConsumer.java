package no.nav.registre.endringsmeldinger.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;
import org.w3c.dom.Document;

import java.util.List;

import no.nav.registre.endringsmeldinger.consumer.rs.exceptions.SyntetiseringsException;

@Slf4j
@Component
public class NavEndringsmeldingerSyntetisererenConsumer {

    private static final ParameterizedTypeReference<List<Document>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    @Autowired
    private RestTemplate restTemplate;

    private UriTemplate serverUrl;

    public NavEndringsmeldingerSyntetisererenConsumer(@Value("${syntrest.rest.api.url}") String syntrestServerUrl) {
        this.serverUrl = new UriTemplate(syntrestServerUrl + "/v1/generate/nav/{endringskode}?numToGenerate={antallMeldinger}");
    }

    public ResponseEntity<List<Document>> getSyntetiserteNavEndringsmeldinger(
            String endringskode,
            int antallMeldinger
    ) {
        var getRequest = RequestEntity.get(serverUrl.expand(endringskode, antallMeldinger)).build();
        ResponseEntity<List<Document>> response;
        try {
            response = restTemplate.exchange(getRequest, RESPONSE_TYPE);
        } catch (Exception e) {
            throw new SyntetiseringsException(e.getMessage(), e.getCause());
        }
        return response;
    }
}
