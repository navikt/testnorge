package no.nav.registre.testnorge.person.consumer.dto.pdl.graphql;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class HentPerson {
    List<Navn> navn;
    List<Foedsel> foedsel;
    List<Bostedsadresse> bostedsadresse;
    List<Folkeregisteridentifikator> folkeregisteridentifikator;
}
