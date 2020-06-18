package no.nav.registre.testnorge.person.domain;

import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

import no.nav.registre.testnorge.dto.person.v1.AdresseDTO;


@RequiredArgsConstructor
public class Adresse {
    private final AdresseDTO dto;

    public String getGatenavn() {
        return dto.getGatenavn();
    }

    public String getAdressenavn () {
        if (getGatenavn() == null) {
            return null;
        }
        List<String> list = splitGatenavn(getGatenavn());
        return list.get(0);
    }

    public String getHusnummer () {
        if (getGatenavn() == null) {
            return null;
        }
        List<String> list = splitGatenavn(getGatenavn());
        return list.size() == 2 ? list.get(1) : null;
    }

    public String getPostnummer() {
        return dto.getPostnummer();
    }

    public String getPoststed() {
        return dto.getPoststed();
    }

    public String getKommunenummer() {
        return dto.getKommunenummer();
    }

    private List<String> splitGatenavn (String gatenavn) {
        return Arrays.asList(gatenavn.split("^\\d+$", 2));
    }
}
