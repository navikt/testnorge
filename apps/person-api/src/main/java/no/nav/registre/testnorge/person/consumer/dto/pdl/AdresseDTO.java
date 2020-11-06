package no.nav.registre.testnorge.person.consumer.dto.pdl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.testnorge.person.domain.Person;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AdresseDTO {
    String kilde;
    String master;
    VegadresseDTO vegadresse;

    public AdresseDTO(Person person, String kilde) {
        vegadresse = person.getAdresse() == null ? null : new VegadresseDTO(person.getAdresse());
        this.kilde = kilde;
        master = "FREG";
    }
}
