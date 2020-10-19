package no.nav.registre.testnorge.originalpopulasjon.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import no.nav.registre.testnorge.libs.dto.person.v1.AdresseDTO;

@Builder
@AllArgsConstructor
@Value
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
