package no.nav.testnav.personfastedataservice.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.Set;

import no.nav.testnav.dto.personfastedataservice.v1.PersonDTO;
import no.nav.testnav.personfastedataservice.repository.model.PersonEntity;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Person {

    public Person(PersonDTO dto){
        ident = dto.getIdent();
        fornavn = dto.getFornavn();
        etternavn = dto.getEtternavn();
        adresse = dto.getAdresse();
        postnr = dto.getPostnr();
        by = dto.getBy();
        opprinnelse = dto.getOpprinnelse();
        tags = dto.getTags();
    }

    String ident;
    String fornavn;
    String etternavn;
    String adresse;
    String postnr;
    String by;
    String opprinnelse;
    Set<String> tags;

    public PersonDTO toDTO() {
        return PersonDTO
                .builder()
                .ident(ident)
                .fornavn(fornavn)
                .etternavn(etternavn)
                .adresse(adresse)
                .postnr(postnr)
                .by(by)
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
