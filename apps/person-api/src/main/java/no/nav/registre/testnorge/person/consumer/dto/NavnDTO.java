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
public class NavnDTO {
    String fornavn;
    String mellomnavn;
    String etternavn;
    String kilde;
    String master;

    OriginaltNavnDTO originaltNavn;

    public NavnDTO(Person person) {
        fornavn = person.getFornavn();
        etternavn = person.getEtternavn();
        mellomnavn = person.getMellomnavn();
        originaltNavn = new OriginaltNavnDTO(person);
        kilde = "DOLLY";
        master = "PDL";
    }
}
