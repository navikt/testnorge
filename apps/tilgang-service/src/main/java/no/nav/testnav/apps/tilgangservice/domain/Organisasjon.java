package no.nav.testnav.apps.tilgangservice.domain;

import java.time.LocalDateTime;

import no.nav.testnav.apps.tilgangservice.client.altinn.v1.dto.OrganisasjonDTO;
import no.nav.testnav.apps.tilgangservice.client.altinn.v1.dto.RightDTO;

public class Organisasjon {
    private final String navn;
    private final String orgnisasjonsnummer;
    private final String orgnisasjonsfrom;
    private final LocalDateTime gyldigTil;

    public Organisasjon(OrganisasjonDTO dto, RightDTO rightDTO) {
        this.navn = dto.Name();
        this.orgnisasjonsnummer = dto.OrganizationNumber();
        this.orgnisasjonsfrom = dto.Type();
        this.gyldigTil = rightDTO.validTo();
    }

    public no.nav.testnav.apps.tilgangservice.controller.dto.OrganisasjonDTO toDTO() {
        return new no.nav.testnav.apps.tilgangservice.controller.dto.OrganisasjonDTO(
                navn,
                orgnisasjonsnummer,
                orgnisasjonsfrom,
                gyldigTil
        );
    }


}
