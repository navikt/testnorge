package no.nav.testnav.apps.tilgangservice.domain;

import java.time.LocalDateTime;

import no.nav.testnav.apps.tilgangservice.client.altinn.v1.dto.OrganisajonDTO;
import no.nav.testnav.apps.tilgangservice.client.altinn.v1.dto.RightDTO;

public class Organisajon {
    private final String navn;
    private final String orgnisajonsnummer;
    private final String orgnisajonsfrom;
    private final LocalDateTime gyldigTil;

    public Organisajon(OrganisajonDTO dto, RightDTO rightDTO) {
        this.navn = dto.Name();
        this.orgnisajonsnummer = dto.OrganizationNumber();
        this.orgnisajonsfrom = dto.Type();
        this.gyldigTil = rightDTO.validTo();
    }

    public no.nav.testnav.apps.tilgangservice.controller.dto.OrganisajonDTO toDTO() {
        return new no.nav.testnav.apps.tilgangservice.controller.dto.OrganisajonDTO(
                navn,
                orgnisajonsnummer,
                orgnisajonsfrom,
                gyldigTil
        );
    }


}
