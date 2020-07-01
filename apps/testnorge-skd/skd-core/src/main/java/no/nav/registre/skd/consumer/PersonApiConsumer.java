package no.nav.registre.skd.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.dto.person.v1.PersonDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.List;


@Component
@Slf4j
@DependencyOn("person-api")
public class PersonApiConsumer {

    private final UriTemplate serverUrl;

    private static final ParameterizedTypeReference<ResponseEntity<?>> RESPONSE_TYPE = new ParameterizedTypeReference<>() {};

    @Autowired
    private RestTemplate restTemplate;

    public PersonApiConsumer(
            @Value("${person-api.rest.api.url}") String personApiUrl
    ) {
        this.serverUrl = new UriTemplate(personApiUrl + "/v1/person");
    }

    public ResponseEntity<?> leggTilIdentIPdl(PersonDTO person) {
        var postRequest = RequestEntity.post(serverUrl.expand()).body(person);
        return restTemplate.exchange(postRequest, RESPONSE_TYPE);
    }

}
