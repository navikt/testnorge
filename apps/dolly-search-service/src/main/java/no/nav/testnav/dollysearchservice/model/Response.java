package no.nav.testnav.dollysearchservice.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Response {
    HentPersonModel hentPerson;
    HentIdenterModel hentIdenter;
    List<String> tags;
}
