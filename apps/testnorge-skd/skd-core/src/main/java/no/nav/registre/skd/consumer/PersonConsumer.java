package no.nav.registre.skd.consumer;

import lombok.extern.slf4j.Slf4j;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;
import no.nav.registre.testnorge.dto.person.v1.PersonDTO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;


@Component
@Slf4j
@DependencyOn("person-api")
public class PersonConsumer {

    private final UriTemplate serverUrl;

    private final RestTemplate restTemplate;

    public PersonConsumer(
            @Value("${person.rest.api.url}") String personApiUrl,
            RestTemplate restTemplate
    ) {
        this.serverUrl = new UriTemplate(personApiUrl + "/v1/person");
        this.restTemplate = restTemplate;
    }

    public ResponseEntity<String> leggTilIdentIPdl(PersonDTO person) {
        var postRequest = RequestEntity.post(serverUrl.expand()).body(person);
        return restTemplate.exchange(postRequest, String.class);
    }

}
