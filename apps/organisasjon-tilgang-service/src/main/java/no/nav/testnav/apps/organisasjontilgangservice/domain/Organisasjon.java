package no.nav.testnav.apps.organisasjontilgangservice.domain;

import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.dto.OrganisasjonDTO;
import no.nav.testnav.apps.organisasjontilgangservice.consumer.altinn.v1.dto.RightDTO;

import java.time.LocalDateTime;

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
