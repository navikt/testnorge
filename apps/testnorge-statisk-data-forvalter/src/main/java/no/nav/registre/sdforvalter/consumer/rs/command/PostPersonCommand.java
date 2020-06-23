package no.nav.registre.sdforvalter.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.Callable;

import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.testnorge.dto.person.v1.AdresseDTO;
import no.nav.registre.testnorge.dto.person.v1.PersonDTO;

@RequiredArgsConstructor
public class PostPersonCommand implements Callable<ResponseEntity<String>> {
    private final RestTemplate restTemplate;
    private final String url;
    private final TpsIdent ident;

    @Override
    public ResponseEntity<String> call() {
        PersonDTO person = PersonDTO
                .builder()
                .ident(ident.getFnr())
                .fornavn(ident.getFirstName())
                .etternavn(ident.getLastName())
                .adresse(AdresseDTO
                        .builder()
                        .gatenavn(ident.getAddress())
                        .postnummer(ident.getPostNr())
                        .poststed(ident.getCity())
                        .build()
                )
                .build();

        ResponseEntity<String> response = restTemplate.postForEntity(url + "/api/v1/person", person, String.class);
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Feil ved opprettelse av person " + ident.getFnr() + ". Status code: " + response.getStatusCodeValue());
        }
        return response;
    }
}
