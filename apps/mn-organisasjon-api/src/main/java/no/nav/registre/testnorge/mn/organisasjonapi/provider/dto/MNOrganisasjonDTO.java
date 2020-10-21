package no.nav.registre.testnorge.mn.organisasjonapi.provider.dto;

import no.nav.registre.testnorge.libs.dto.organisasjon.v1.AdresseDTO;
import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;

public class MNOrganisasjonDTO extends OrganisasjonDTO {
    public MNOrganisasjonDTO(OrganisasjonDTO dto, Boolean active) {
        super(dto);
        this.active = active;
    }

    private final Boolean active;

    public MNOrganisasjonDTO() {
        active = null;
    }

    public MNOrganisasjonDTO(
            String orgnummer, String enhetType, String navn, String juridiskEnhet, AdresseDTO postadresse, AdresseDTO forretningsadresser, String redigertnavn, Boolean active) {
        super(orgnummer, enhetType, navn, juridiskEnhet, postadresse, forretningsadresser, redigertnavn);
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }
}

