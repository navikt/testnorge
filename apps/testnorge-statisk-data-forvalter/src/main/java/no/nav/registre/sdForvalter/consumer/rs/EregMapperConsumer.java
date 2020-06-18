package no.nav.registre.sdForvalter.consumer.rs;

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

import no.nav.registre.sdForvalter.consumer.rs.request.ereg.EregMapperRequest;
import no.nav.registre.sdForvalter.domain.Ereg;
import no.nav.registre.sdForvalter.domain.EregListe;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;

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

    public void create(EregListe eregListe, String env) {
        log.info("Oppretter EREG med {} nye organisasjoner...", eregListe.getListe().size());
        uploadToEreg(eregListe, env, false);
    }

    public void update(Ereg ereg, String env) {
        update(new EregListe(ereg), env);
    }

    public void update(EregListe eregListe, String env) {
        log.info("Oppdaterer EREG med {} organisasjoner...", eregListe.getListe().size());
        uploadToEreg(eregListe, env, true);
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

    private void uploadToEreg(EregListe eregListe, String env, boolean update) {
        UriTemplate uriTemplate = new UriTemplate(eregUrl + "/orkestrering/opprett?lastOpp=true&miljoe={miljoe}");
        RequestEntity<List<EregMapperRequest>> requestEntity = new RequestEntity<>(
                eregListe.getListe()
                        .stream()
                        .map(item -> new EregMapperRequest(item, update))
                        .collect(Collectors.toList()),
                HttpMethod.POST, uriTemplate.expand(env));
        ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
        if (response.getBody() != null && response.getStatusCode() != HttpStatus.OK) {
            log.error("Klarte ikke å opprette alle eregs. Response http status: {})", response.getStatusCode());
        }
    }

    public EregListe flatfilToJSON(String flatfil) {
        UriTemplate uriTemplate = new UriTemplate(eregUrl + "/organisasjoner");
        try {
            RequestEntity<String> requestEntity = new RequestEntity<>(
                    flatfil,
                    HttpMethod.POST,
                    uriTemplate.expand());
            ResponseEntity<List<EregMapperRequest>> responseEntity = restTemplate.exchange(requestEntity, RESPONSE_TYPE);

            return new EregListe(responseEntity.getBody()
                    .stream()
                    .map(Ereg::new)
                    .collect(Collectors.toList()));
        } catch (Exception e) {
            log.error("Klarte ikke å hente organisasjoner fra flatfil", e);
            throw e;
        }
    }
}
