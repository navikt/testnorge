package no.nav.registre.sdforvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.sdforvalter.consumer.rs.request.KrrRequest;
import no.nav.registre.sdforvalter.domain.Krr;
import no.nav.registre.sdforvalter.domain.KrrListe;
import no.nav.registre.testnorge.libs.dependencyanalysis.DependencyOn;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.net.URI;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Component
@DependencyOn(value = "krr-stub", external = true)
public class KrrConsumer {

    private static final ParameterizedTypeReference<List<KrrRequest>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {
    };

    private final RestTemplate restTemplate;
    private final URI krrUrl;

    public KrrConsumer(RestTemplate restTemplate, @Value("${krr.stub.rest.api.url}") String krrUrl) {
        this.restTemplate = restTemplate;
        this.krrUrl = new UriTemplate(krrUrl + "/v1/kontaktinformasjon").expand();
    }

    public Set<String> send(KrrListe liste) {
        Set<String> existing = existingKrrFnr(liste
                .getListe()
                .stream()
                .map(Krr::getFnr)
                .collect(Collectors.toSet())
        );
        List<Krr> noneExisting = liste
                .getListe()
                .stream()
                .filter(krr -> !existing.contains(krr.getFnr()))
                .collect(Collectors.toList());

        if (noneExisting.isEmpty()) {
            log.info("Fant ingen nye krr.");
            return Collections.emptySet();
        }

        Set<KrrRequest> requestData = noneExisting
                .stream()
                .map(KrrRequest::new)
                .collect(Collectors.toSet());

        HttpHeaders httpHeaders = buildDefaultHeaders();

        Set<String> createdInKrr = new HashSet<>();
        requestData.forEach(d -> {
            RequestEntity<KrrRequest> requestEntity = new RequestEntity<>(d, httpHeaders, HttpMethod.POST, krrUrl);
            try {
                ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
                if (response.getStatusCode() != HttpStatus.CREATED) {
                    log.error("Klarte ikke opprette kontaktinformasjon i krr-stub");
                    return;
                }
            } catch (Exception e){
                log.error("Klarte ikke opprette kontaktinformasjon i krr-stub", e);
                return;
            }
            createdInKrr.add(d.getFnr());
        });
        return createdInKrr;
    }


    private Set<String> existingKrrFnr(Set<String> fnrs) {
        Set<String> existing = new HashSet<>();
        HttpHeaders httpHeaders = buildDefaultHeaders();

        fnrs.forEach(fnr -> {
            httpHeaders.add("Nav-Personident", fnr);
            var requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, krrUrl);
            try {
                ResponseEntity<List<KrrRequest>> response = restTemplate.exchange(requestEntity, RESPONSE_TYPE);
                if (response.getStatusCode() == HttpStatus.OK) {
                    if (response.getBody() != null) {
                        existing.addAll(response.getBody().stream().map(KrrRequest::getFnr).collect(Collectors.toSet()));
                    }
                }
            } catch (HttpClientErrorException ignored) {
                log.info("Fant ikke person {}", fnr);
            }
        });

        return existing;
    }

    private HttpHeaders buildDefaultHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Nav-Call-Id", "Synt");
        httpHeaders.add("Nav-Consumer-Id", "Synt");
        return httpHeaders;
    }
}
