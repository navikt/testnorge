package no.nav.testnav.apps.personservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

import no.nav.testnav.apps.personservice.consumer.dto.pdl.graphql.Vegadresse;
import no.nav.testnav.apps.personservice.consumer.dto.tpsf.Boadresse;
import no.nav.testnav.dto.personservice.v1.AdresseDTO;

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
        gatenavn = dto.getGatenavn();
        postnummer = dto.getPostnummer();
        poststed = dto.getPoststed();
        kommunenummer = dto.getKommunenummer();
    }

    public Adresse(Vegadresse vegadresse) {
        gatenavn = vegadresse.getAdressenavn() + " " + vegadresse.getHusnummer();
        postnummer = vegadresse.getPostnummer();
        kommunenummer = vegadresse.getKommunenummer();
    }

    public Adresse (Boadresse boadresse) {
        gatenavn = boadresse.getGateadresse() + " " + boadresse.getHusnummer();
        postnummer = boadresse.getPostnr();
    }

    public AdresseDTO toDto() {
        return AdresseDTO.builder()
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
        String muligHusnummer = list.get(list.size() - 1);

        return StringUtils.isNumeric(muligHusnummer) ? muligHusnummer : null;
    }


    private List<String> splitGatenavn(String gatenavn) {
        return Arrays.asList(StringUtils.split(gatenavn));
    }
}
