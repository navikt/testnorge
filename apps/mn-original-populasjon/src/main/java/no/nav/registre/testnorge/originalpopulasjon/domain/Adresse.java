package no.nav.registre.testnorge.originalpopulasjon.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import no.nav.testnav.libs.dto.person.v1.AdresseDTO;

@Value
@AllArgsConstructor
@Builder
public class Adresse {
    String gatenavn;
    String postnummer;
    String poststed;
    String kommunenummer;

    public Adresse(AdresseDTO dto) {
        gatenavn = dto.getGatenavn();
        postnummer = dto.getPostnummer();
        poststed = dto.getPoststed();
        kommunenummer = dto.getKommunenummer();
    }

    public AdresseDTO toDto() {
        return AdresseDTO.builder()
                .gatenavn(gatenavn)
                .postnummer(postnummer)
                .poststed(poststed)
                .kommunenummer(kommunenummer)
                .build();
    }
}
