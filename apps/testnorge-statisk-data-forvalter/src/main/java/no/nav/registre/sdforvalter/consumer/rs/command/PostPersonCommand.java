package no.nav.registre.sdforvalter.consumer.rs.command;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplate;

import java.util.Collections;
import java.util.concurrent.Callable;

import no.nav.registre.sdforvalter.domain.TpsIdent;
import no.nav.registre.sdforvalter.exception.FeilVedOpprettelseException;
import no.nav.registre.testnorge.libs.dto.person.v1.AdresseDTO;
import no.nav.registre.testnorge.libs.dto.person.v1.PersonDTO;

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
                        .build())
                .build();

        var kilde = ident.getOpprinnelse();
        UriTemplate uriTemplate = new UriTemplate(url + "/api/v1/personer?kilde={kilde}");
        var uriVariables = Collections.singletonMap("kilde", kilde);

        var response = restTemplate.exchange(
                uriTemplate.expand(uriVariables),
                HttpMethod.POST,
                new HttpEntity<>(person),
                String.class
        );
        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new FeilVedOpprettelseException("Feil ved opprettelse av person " + ident.getFnr() + ". Status code: " + response.getStatusCodeValue());
        }
        return response;
    }
}
