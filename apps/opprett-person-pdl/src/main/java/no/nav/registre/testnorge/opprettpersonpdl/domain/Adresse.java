package no.nav.registre.testnorge.opprettpersonpdl.domain;

import lombok.Value;

import no.nav.testnav.libs.dto.person.v1.AdresseDTO;

@Value
public class Adresse {
    public Adresse(no.nav.registre.testnorge.libs.avro.person.Adresse adresse) {
        gatenavn = adresse.getGatenavn() != null ? adresse.getGatenavn().toString() : null;
        postnummer = adresse.getPostnummer() != null ? adresse.getPostnummer().toString() : null;
        poststed = adresse.getPoststed() != null ? adresse.getPoststed().toString() : null;
        kommunenummer = adresse.getKommunenummer() != null ? adresse.getKommunenummer().toString() : null;
    }

    String gatenavn;
    String postnummer;
    String poststed;
    String kommunenummer;

    public AdresseDTO toDTO() {
        return AdresseDTO
                .builder()
                .gatenavn(gatenavn)
                .postnummer(postnummer)
                .poststed(poststed)
                .kommunenummer(kommunenummer)
                .build();
    }

}