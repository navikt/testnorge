package no.nav.testnav.apps.profilapi.domain;

import lombok.RequiredArgsConstructor;

import no.nav.testnav.apps.profilapi.consumer.dto.ProfileDTO;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;

@RequiredArgsConstructor
public class Profil {
    private final String visningsNavn;
    private final String epost;
    private final String avdeling;

    public Profil(ProfileDTO dto) {
        this.visningsNavn = dto.getDisplayName();
        this.epost = dto.getMail();
        this.avdeling = dto.getOfficeLocation();
    }

    public ProfilDTO toDTO() {
        return ProfilDTO
                .builder()
                .visningsNavn(visningsNavn)
                .epost(epost)
                .avdeling(avdeling)
                .build();
    }
}