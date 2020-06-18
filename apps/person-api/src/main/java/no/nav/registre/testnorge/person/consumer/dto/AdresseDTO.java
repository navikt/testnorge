package no.nav.registre.testnorge.person.consumer.dto;

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
VegadresseDTO vegadresseDTO;


    public AdresseDTO(Person person) {
        vegadresseDTO = new VegadresseDTO(person.getAdresse());
        kilde = "DOLLY";
        master = "PDL";
    }
}
