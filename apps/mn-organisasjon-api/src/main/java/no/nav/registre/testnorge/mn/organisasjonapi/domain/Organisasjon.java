package no.nav.registre.testnorge.mn.organisasjonapi.domain;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.mn.organisasjonapi.provider.dto.MNOrganisasjonDTO;
import no.nav.registre.testnorge.mn.organisasjonapi.repository.model.OrganisasjonModel;

@RequiredArgsConstructor
public class Organisasjon {
    private final MNOrganisasjonDTO dto;

    public Organisasjon(OrganisasjonDTO dto, boolean active) {
        this.dto = new MNOrganisasjonDTO(dto, active);
    }

    public MNOrganisasjonDTO toDTO() {
        return dto;
    }

    public OrganisasjonModel toModel(){
        return OrganisasjonModel
                .builder()
                .active(dto.getActive())
                .orgnummer(dto.getOrgnummer())
                .build();
    }

}
