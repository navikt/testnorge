package no.nav.testnav.personfastedataservice.domain;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.testnav.libs.dto.personservice.v1.AdresseDTO;

@Value
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class Adresse {
    String gatenavn;
    String postnummer;
    String poststed;
    String kommunenummer;

    public Adresse(AdresseDTO dto){
        gatenavn = dto.getGatenavn();
        postnummer = dto.getPostnummer();
        poststed = dto.getPoststed();
        kommunenummer = dto.getKommunenummer();
    }

    public AdresseDTO toDTO(){
        return AdresseDTO
                .builder()
                .gatenavn(gatenavn)
                .postnummer(postnummer)
                .poststed(poststed)
                .kommunenummer(kommunenummer)
                .build();
    }
}
