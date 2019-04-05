package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.time.Instant;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.consumer.rs.request.KrrRequest;
import no.nav.registre.sdForvalter.database.model.KrrModel;

@Slf4j
@Component
public class KrrConsumer {

    private static final ParameterizedTypeReference<List<KrrRequest>> RESPONSE_TYPE = new ParameterizedTypeReference<List<KrrRequest>>() {
    };

    private final RestTemplate restTemplate;
    private final String krrUrl;

    public KrrConsumer(RestTemplate restTemplate, @Value("${krr-stub.rest.api.url}") String krrUrl) {
        this.restTemplate = restTemplate;
        this.krrUrl = krrUrl + "/api/v1";
    }

    public Set<String> send(Set<KrrModel> data) {

        String dateString = new Date(Instant.now().toEpochMilli()).toString();

        Set<KrrRequest> requestData = data.parallelStream().map(k -> {
            KrrRequest.KrrRequestBuilder builder = KrrRequest.builder();
            builder.email(k.getEmail());
            if (k.isEmailValid()) {
                builder.emailValidatedDate(dateString);
                builder.emailUpdated(dateString);
            }
            builder.phone(k.getSms());
            if (k.isSmsValid()) {
                builder.phoneValidatedDate(dateString);
                builder.phoneUpdated(dateString);
            }
            builder.reserved(k.isReserved());
            builder.fnr(k.getFnr());
            return builder.build();
        }).collect(Collectors.toSet());

        UriTemplate uriTemplate = new UriTemplate(krrUrl + "/kontaktinformasjon");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Nav-Call-Id", "Synt");
        httpHeaders.add("Nav-Consumer-Id", "Synt");

        Set<String> createdInKrr = new HashSet<>();
        requestData.forEach(d -> {
            RequestEntity<KrrRequest> requestEntity = new RequestEntity<>(d, httpHeaders, HttpMethod.POST, uriTemplate.expand());
            ResponseEntity<String> response = restTemplate.exchange(requestEntity, String.class);
            if (response.getStatusCode() != HttpStatus.CREATED) {
                log.warn("Klarte ikke opprette kontaktinformasjon i krr-stub");
                return;
            }
            createdInKrr.add(d.getFnr());
        });
        return createdInKrr;
    }


    public Set<String> getContactInformation(Set<String> fnrs) {

        Set<String> existing = new HashSet<>();

        UriTemplate uriTemplate = new UriTemplate(krrUrl + "/kontaktinformasjon");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Nav-Call-Id", "Synt");
        httpHeaders.add("Nav-Consumer-Id", "Synt");

        fnrs.forEach(fnr -> {
            httpHeaders.add("Nav-Personident", fnr);
            RequestEntity requestEntity = new RequestEntity<>(httpHeaders, HttpMethod.GET, uriTemplate.expand());
            ResponseEntity<List<KrrRequest>> response = restTemplate.exchange(requestEntity, RESPONSE_TYPE);
            if (response.getStatusCode() == HttpStatus.OK) {
                if (response.getBody() != null) {
                    existing.addAll(response.getBody().stream().map(KrrRequest::getFnr).collect(Collectors.toSet()));
                }
            }
        });

        return existing;
    }
}
