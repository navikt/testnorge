package no.nav.testnav.personfastedataservice.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Set;

import no.nav.testnav.libs.dto.personservice.v1.PersonDTO;
import no.nav.testnav.libs.dto.personservice.v1.Gruppe;
import no.nav.testnav.personfastedataservice.repository.model.PersonEntity;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Person {

    public Person(PersonDTO dto){
        ident = dto.getIdent();
        fornavn = dto.getFornavn();
        etternavn = dto.getEtternavn();
        adresse = dto.getAdresse() == null ? null : new Adresse(dto.getAdresse());
        opprinnelse = dto.getOpprinnelse();
        tags = dto.getTags();
    }

    String ident;
    String fornavn;
    String etternavn;
    Adresse adresse;
    String opprinnelse;
    Set<String> tags;

    public PersonDTO toDTO() {
        return PersonDTO
                .builder()
                .ident(ident)
                .fornavn(fornavn)
                .etternavn(etternavn)
                .adresse(adresse == null ? null : adresse.toDTO())
                .opprinnelse(opprinnelse)
                .tags(tags)
                .build();
    }

    public PersonEntity toEntity(Gruppe gruppe) {
        return PersonEntity
                .builder()
                .ident(ident)
                .person(this)
                .gruppe(gruppe)
                .build();
    }

}
