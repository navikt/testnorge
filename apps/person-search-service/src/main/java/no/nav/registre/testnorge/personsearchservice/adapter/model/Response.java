package no.nav.registre.testnorge.personsearchservice.adapter.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Response {
    HentPerson hentPerson;
    HentIdenter hentIdenter;
    List<String> tags;
}
