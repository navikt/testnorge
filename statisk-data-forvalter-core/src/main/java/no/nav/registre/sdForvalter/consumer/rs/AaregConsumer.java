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

import java.util.Set;

import no.nav.registre.sdForvalter.database.model.AaregModel;

@Slf4j
@Component
public class AaregConsumer {

    private final RestTemplate restTemplate;
    private final String aaregUrl;

    public AaregConsumer(RestTemplate restTemplate, @Value("${testnorge-aareg.rest.api.url}") String aaregUrl) {
        this.restTemplate = restTemplate;
        this.aaregUrl = aaregUrl + "/api/v1";
    }

    public boolean send(Set<AaregModel> data, String environment) {
        UriTemplate uriTemplate = new UriTemplate(aaregUrl + "/lagreFastArbeidsforhold?miljoe={miljoe}");
        RequestEntity<Set<AaregModel>> request = new RequestEntity<>(data, HttpMethod.POST, uriTemplate.expand(environment));
        ResponseEntity response = restTemplate.exchange(request, String.class);
        if (response.getStatusCode() != HttpStatus.OK) {
            log.warn("Klarte ikke opprette arbeidsforhold for {}", data);
            return false;
        }
        return true;
    }
}
