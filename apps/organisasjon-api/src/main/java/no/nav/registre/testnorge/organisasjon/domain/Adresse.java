package no.nav.registre.testnorge.organisasjon.domain;

import no.nav.registre.testnorge.organisasjon.consumer.dto.AdresseDTO;

public class Adresse {
    private final String kommunenummer;

    public Adresse(AdresseDTO dto) {
        this.kommunenummer = dto.getKommunenummer();
    }

    public no.nav.registre.testnorge.dto.organisasjon.v1.AdresseDTO toDTO() {
        return no.nav.registre.testnorge.dto.organisasjon.v1.AdresseDTO.builder()
                .kommunenummer(kommunenummer)
                .build();
    }
}
