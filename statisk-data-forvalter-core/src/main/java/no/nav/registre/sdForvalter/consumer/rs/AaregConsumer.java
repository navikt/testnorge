package no.nav.registre.sdForvalter.consumer.rs;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import no.nav.registre.sdForvalter.consumer.rs.request.AaregRequest.AaregRequest;
import no.nav.registre.sdForvalter.consumer.rs.request.AaregRequest.Arbeidsforhold;
import no.nav.registre.sdForvalter.consumer.rs.request.AaregRequest.Arbeidsgiver;
import no.nav.registre.sdForvalter.consumer.rs.request.AaregRequest.Arbeidstaker;
import no.nav.registre.sdForvalter.consumer.rs.response.AaregResponse;
import no.nav.registre.sdForvalter.database.model.AaregModel;

@Slf4j
@Component
public class AaregConsumer {

    private final RestTemplate restTemplate;
    private final String aaregUrl;

    public AaregConsumer(RestTemplate restTemplate, @Value("${testnorge.aareg.rest.api.url}") String aaregUrl) {
        this.restTemplate = restTemplate;
        this.aaregUrl = aaregUrl + "/v1/syntetisering";
    }

    public Map<String, String> send(Set<AaregModel> data, String environment) {

        List<AaregRequest> requestBody = data.parallelStream().map(d -> new AaregRequest(
                new Arbeidsforhold(
                        new Arbeidsgiver(
                                Long.toString(d.getOrgId())
                        ),
                        new Arbeidstaker(
                                d.getFnr()
                        )
                ),
                Collections.singletonList(environment)
        )).collect(Collectors.toList());

        UriTemplate uriTemplate = new UriTemplate(aaregUrl + "/sendTilAareg?fyllUtArbeidsforhold=true");
        RequestEntity<List<AaregRequest>> request = new RequestEntity<>(requestBody, HttpMethod.POST, uriTemplate.expand(environment));
        ResponseEntity<AaregResponse> response = restTemplate.exchange(request, AaregResponse.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            if (response.getBody() != null) {
                log.warn("Klarte ikke opprette arbeidsforhold for {}", response.getBody().getIdenterSomIkkeKunneLagresIAareg());
            }
        }
        return Objects.requireNonNull(response.getBody()).getIdenterSomIkkeKunneLagresIAareg();
    }
}
