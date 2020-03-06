package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.consumer.rs.request.ereg.EregMapperRequest;
import no.nav.registre.sdForvalter.database.model.EregModel;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.EregListe;

@Component
@Slf4j
public class EregMapperConsumer {

    private final RestTemplate restTemplate;
    private final String eregUrl;

    public EregMapperConsumer(
            RestTemplate restTemplate,
            @Value("${testnorge.ereg.mapper.rest.api.url}") String eregUrl
    ) {
        this.restTemplate = restTemplate;
        this.eregUrl = eregUrl + "/v1";
    }

    public void create(EregListe eregListe, String env) {
        uploadToEreg(eregListe, env);
    }


    private void uploadToEreg(EregListe eregListe, String env) {
        UriTemplate uriTemplate = new UriTemplate(eregUrl + "/orkestrering/opprett?lastOpp=true&miljoe={miljoe}");
        RequestEntity<List<EregMapperRequest>> requestEntity = new RequestEntity<>(
                eregListe.getListe()
                        .stream()
                        .map(EregMapperRequest::new)
                        .collect(Collectors.toList()),
                HttpMethod.POST, uriTemplate.expand(env));
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        if (response.getBody() != null && response.getStatusCode() != HttpStatus.OK) {
            log.error("Klarte ikke å opprette alle eregs. Response http status: {})", response.getStatusCode());
        }
    }
}
