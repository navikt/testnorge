package no.nav.testnav.apps.persontilgangservice.domain;

import no.nav.testnav.apps.persontilgangservice.client.altinn.v1.dto.AccessDTO;
import no.nav.testnav.apps.persontilgangservice.controller.dto.OrganisasjonDTO;

public class Access {
    private final String navn;
    private final String organisajonsnummer;
    private final String organisajonsfrom;

    public Access(AccessDTO accessDTO) {
        this.navn = accessDTO.name();
        this.organisajonsnummer = accessDTO.organizationNumber();
        this.organisajonsfrom = accessDTO.organizationForm();
    }

    public OrganisasjonDTO toDTO() {
        return new OrganisasjonDTO(
                navn,
                organisajonsnummer,
                organisajonsfrom,
                null // TODO Set gyldigTil
        );
    }

    public String getOrganisajonsnummer() {
        return organisajonsnummer;
    }
}
