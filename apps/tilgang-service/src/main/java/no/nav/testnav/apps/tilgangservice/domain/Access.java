package no.nav.testnav.apps.tilgangservice.domain;

import no.nav.testnav.apps.tilgangservice.client.altinn.v1.dto.AccessDTO;
import no.nav.testnav.apps.tilgangservice.controller.dto.OrganisajonDTO;

public class Access {
    private final String navn;
    private final String orgnisajonsnummer;
    private final String orgnisajonsfrom;

    public Access(AccessDTO accessDTO) {
        this.navn = accessDTO.name();
        this.orgnisajonsnummer = accessDTO.organizationNumber();
        this.orgnisajonsfrom = accessDTO.organizationForm();
    }

    public OrganisajonDTO toDTO() {
        return new OrganisajonDTO(
                navn,
                orgnisajonsnummer,
                orgnisajonsfrom,
                null // TODO Set gyldigTil
        );
    }

    public String getOrgnisajonsnummer() {
        return orgnisajonsnummer;
    }
}
