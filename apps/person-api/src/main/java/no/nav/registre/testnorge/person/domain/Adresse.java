package no.nav.registre.testnorge.person.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import no.nav.registre.testnorge.person.consumer.dto.AdresseDTO;
import no.nav.registre.testnorge.person.consumer.dto.VegadresseDTO;
import no.nav.registre.testnorge.person.consumer.dto.graphql.Vegadresse;

import java.util.Arrays;
import java.util.List;

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
        VegadresseDTO vegadresseDTO = dto.getVegadresse();
        Adresse.builder()
                .gatenavn(vegadresseDTO.getAdressenavn() + " " + vegadresseDTO.getHusnummer())
                .postnummer(vegadresseDTO.getPostnummer())
                .poststed(null)
                .kommunenummer(vegadresseDTO.getKommunenummer())
                .build();
    }

    public Adresse(no.nav.registre.testnorge.dto.person.v1.AdresseDTO dto) {
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

    public no.nav.registre.testnorge.dto.person.v1.AdresseDTO toDto() {
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
