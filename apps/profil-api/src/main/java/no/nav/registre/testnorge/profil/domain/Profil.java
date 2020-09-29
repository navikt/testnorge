package no.nav.registre.testnorge.profil.domain;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.libs.dto.profil.v1.ProfilDTO;
import no.nav.registre.testnorge.profil.consumer.dto.ProfileDTO;

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