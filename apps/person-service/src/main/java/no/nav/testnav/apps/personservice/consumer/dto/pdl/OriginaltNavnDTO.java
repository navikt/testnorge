package no.nav.testnav.apps.personservice.consumer.dto.pdl;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.testnav.apps.personservice.domain.Person;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class OriginaltNavnDTO {
    String fornavn;
    String mellomnavn;
    String etternavn;

    public OriginaltNavnDTO(Person person) {
        fornavn = person.getFornavn();
        etternavn = person.getEtternavn();
        mellomnavn = person.getMellomnavn();
    }
}
