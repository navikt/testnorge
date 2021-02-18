package no.nav.registre.sdforvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;
import java.util.stream.Collectors;

import no.nav.registre.sdforvalter.consumer.rs.request.ereg.EregMapperRequest;
import no.nav.registre.sdforvalter.domain.Ereg;
import no.nav.registre.sdforvalter.domain.EregListe;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;

@Component
@Slf4j
@DependencyOn("testnorge-ereg-mapper")
public class EregMapperConsumer {

    private final RestTemplate restTemplate;
    private final String eregUrl;
    private static final ParameterizedTypeReference<List<EregMapperRequest>> RESPONSE_TYPE = new ParameterizedTypeReference<List<EregMapperRequest>>() {
    };

    public EregMapperConsumer(
            RestTemplate restTemplate,
            @Value("${testnorge.ereg.mapper.rest.api.url}") String eregUrl
    ) {
        this.restTemplate = restTemplate;
        this.eregUrl = eregUrl + "/v1";
    }

    public String generateFlatfil(EregListe eregListe, boolean update) {
        UriTemplate uriTemplate = new UriTemplate(eregUrl + "/orkestrering/generer");
        try {
            RequestEntity<List<EregMapperRequest>> requestEntity = new RequestEntity<>(
                    eregListe.getListe()
                            .stream()
                            .map(item -> new EregMapperRequest(item, update))
                            .collect(Collectors.toList()),
                    HttpMethod.POST, uriTemplate.expand());
            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
            return response.getBody();
        } catch (Exception e) {
            log.error("Klarte ikke å generere flatfil.", e);
            throw e;
        }
    }
}
