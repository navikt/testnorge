package no.nav.registre.testnorge.person.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;

import no.nav.registre.testnorge.person.consumer.dto.graphql.Vegadresse;
import no.nav.registre.testnorge.dto.person.v1.AdresseDTO;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Adresse {

    private String gatenavn;
    private String postnummer;
    private String poststed;
    private String kommunenummer;

    public Adresse(AdresseDTO dto) {
        Adresse.builder()
                .gatenavn(dto.getGatenavn())
                .postnummer(dto.getPostnummer())
                .poststed(dto.getPoststed())
                .kommunenummer(dto.getKommunenummer())
                .build();
    }

    public Adresse(Vegadresse vegadresse) {
        Adresse.builder()
                .gatenavn(vegadresse.getAdressenavn() + " " + vegadresse.getHusnummer())
                .postnummer(vegadresse.getPostnummer())
                .kommunenummer(vegadresse.getKommunenummer())
                .build();
    }

    public AdresseDTO toDto() {
        return no.nav.registre.testnorge.dto.person.v1.AdresseDTO.builder()
                .gatenavn(gatenavn)
                .postnummer(postnummer)
                .poststed(poststed)
                .kommunenummer(kommunenummer)
                .build();
    }

    public String getAdressenavn() {
        if (getGatenavn() == null) {
            return null;
        }
        List<String> list = splitGatenavn(getGatenavn());
        return list.get(0);
    }

    public String getHusnummer() {
        if (getGatenavn() == null) {
            return null;
        }
        List<String> list = splitGatenavn(getGatenavn());
        return list.size() == 2 ? list.get(1) : null;
    }

    private List<String> splitGatenavn(String gatenavn) {
        return Arrays.asList(gatenavn.split("^\\d+$", 2));
    }
}
