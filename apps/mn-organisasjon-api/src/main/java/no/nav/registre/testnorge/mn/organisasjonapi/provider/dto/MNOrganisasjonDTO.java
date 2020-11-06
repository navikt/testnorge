package no.nav.registre.testnorge.mn.organisasjonapi.provider.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

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


    public MNOrganisasjonDTO(String orgnummer, String enhetType, String navn, String juridiskEnhet, AdresseDTO postadresse, AdresseDTO forretningsadresser, String redigertnavn, List<String> driverVirksomheter, Boolean active) {
        super(orgnummer, enhetType, navn, juridiskEnhet, postadresse, forretningsadresser, redigertnavn, driverVirksomheter);
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    @JsonIgnore
    public boolean isOpplysningspliktig() {
        switch (getEnhetType()) {
            case "AS":
            case "NUF":
            case "BRL":
            case "KBO":
            case "SA":
            case "ENK":
                return true;
            default:
                return false;
        }
    }
}

