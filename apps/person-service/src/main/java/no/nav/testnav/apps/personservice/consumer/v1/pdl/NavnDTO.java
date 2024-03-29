package no.nav.testnav.apps.personservice.consumer.v1.pdl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.testnav.apps.personservice.domain.Person;

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
        master = "FREG";
    }

    public NavnDTO(Person person, String kilde) {
        fornavn = person.getFornavn();
        etternavn = person.getEtternavn();
        mellomnavn = person.getMellomnavn();
        originaltNavn = new OriginaltNavnDTO(person);
        this.kilde = kilde;
        master = "FREG";
    }
}
