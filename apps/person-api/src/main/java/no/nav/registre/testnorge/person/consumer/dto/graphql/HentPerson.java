package no.nav.registre.testnorge.person.consumer.dto.graphql;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HentPerson {

    List<Navn> navn;
    List<Bostedsadresse> bostedsadresse;
    List<Folkeregisteridentifikator> folkeregisteridentifikator;
}
