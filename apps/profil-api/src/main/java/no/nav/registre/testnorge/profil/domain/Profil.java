package no.nav.registre.testnorge.profil.domain;

import lombok.RequiredArgsConstructor;

import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;
import no.nav.registre.testnorge.profil.consumer.dto.ProfileDTO;

@RequiredArgsConstructor
public class Profil {
    private final String visningsNavn;
    private final String epost;
    private final String avdeling;
    private final String organisasjon;
    private final String type;

    public Profil(ProfileDTO dto) {
        this.visningsNavn = dto.getDisplayName();
        this.epost = dto.getMail();
        this.avdeling = dto.getOfficeLocation();
        this.organisasjon = "NAV";
        this.type = "AzureAD";
    }

    public ProfilDTO toDTO() {
        return ProfilDTO
                .builder()
                .visningsNavn(visningsNavn)
                .epost(epost)
                .avdeling(avdeling)
                .organisasjon(organisasjon)
                .type(type)
                .build();
    }
}