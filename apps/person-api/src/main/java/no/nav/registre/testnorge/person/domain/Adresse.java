package no.nav.registre.testnorge.person.domain;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.dto.person.v1.AdresseDTO;


@RequiredArgsConstructor
public class Adresse {
    private final AdresseDTO dto;

    public String getGatenavn() {
        return dto.getGatenavn();
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
}
