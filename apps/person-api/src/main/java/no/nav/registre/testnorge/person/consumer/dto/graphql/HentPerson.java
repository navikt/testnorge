package no.nav.registre.testnorge.person.consumer.dto.graphql;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HentPerson {

    Navn navn;
    Bostedsadresse bostedsadresse;
    Folkeregisteridentifikator folkeregisteridentifikator;
}
