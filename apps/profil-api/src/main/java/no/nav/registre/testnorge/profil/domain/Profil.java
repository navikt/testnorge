package no.nav.registre.testnorge.profil.domain;

import lombok.RequiredArgsConstructor;
import no.nav.registre.testnorge.profil.consumer.dto.ProfileDTO;
import no.nav.testnav.libs.dto.profil.v1.ProfilDTO;

@RequiredArgsConstructor
public class Profil {
    private final String visningsNavn;
    private final String epost;
    private final String avdeling;
    private final String organisasjon;
    private final String orgnummer;
    private final String type;

    public Profil(ProfileDTO dto) {
        this.visningsNavn = dto.getDisplayName();
        this.epost = dto.getMail();
        this.avdeling = dto.getOfficeLocation();
        this.organisasjon = "NAV";
        this.orgnummer = dto.getOrgnummer();
        this.type = "AzureAD";
    }

    public ProfilDTO toDTO() {
        return ProfilDTO
                .builder()
                .visningsNavn(visningsNavn)
                .epost(epost)
                .avdeling(avdeling)
                .organisasjon(organisasjon)
                .orgnummer(orgnummer)
                .type(type)
                .build();
    }
}