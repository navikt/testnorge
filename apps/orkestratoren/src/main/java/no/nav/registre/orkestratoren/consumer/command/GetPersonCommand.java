package no.nav.registre.orkestratoren.consumer.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.concurrent.Callable;

import no.nav.registre.orkestratoren.consumer.dto.PersonDTO;
import no.nav.registre.testnorge.dependencyanalysis.DependencyOn;

@Slf4j
@DependencyOn("person-api")
@RequiredArgsConstructor
public class GetPersonCommand implements Callable<PersonDTO> {
    private final RestTemplate restTemplate;
    private final String url;
    private final String ident;

    @Override
    public PersonDTO call() {
        var request = RequestEntity.get(URI.create(this.url + "/api/v1/personer/" + ident)).build();
        ResponseEntity<PersonDTO> response = restTemplate.exchange(request, PersonDTO.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Klarer ikke a hente ut person " + ident);
        }
        return response.getBody();
    }
}
