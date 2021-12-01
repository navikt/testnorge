package no.nav.testnav.apps.organisasjontilgangservice.domain;

import java.time.LocalDateTime;

import no.nav.testnav.apps.organisasjontilgangservice.client.altinn.v1.dto.OrganisasjonDTO;
import no.nav.testnav.apps.organisasjontilgangservice.client.altinn.v1.dto.RightDTO;

public class Organisasjon {
    private final String navn;
    private final String organisasjonsnummer;
    private final String organisasjonsfrom;
    private final LocalDateTime gyldigTil;

    public Organisasjon(OrganisasjonDTO dto, RightDTO rightDTO) {
        this.navn = dto.Name();
        this.organisasjonsnummer = dto.OrganizationNumber();
        this.organisasjonsfrom = dto.Type();
        this.gyldigTil = rightDTO.validTo();
    }

    public no.nav.testnav.apps.organisasjontilgangservice.controller.dto.OrganisasjonDTO toDTO() {
        return new no.nav.testnav.apps.organisasjontilgangservice.controller.dto.OrganisasjonDTO(
                navn,
                organisasjonsnummer,
                organisasjonsfrom,
                gyldigTil
        );
    }
}
