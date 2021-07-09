package no.nav.registre.testnorge.mn.organisasjonapi.domain;

import lombok.RequiredArgsConstructor;

import no.nav.testnav.libs.dto.organisasjon.v1.OrganisasjonDTO;
import no.nav.registre.testnorge.mn.organisasjonapi.provider.dto.MNOrganisasjonDTO;
import no.nav.registre.testnorge.mn.organisasjonapi.repository.model.OrganisasjonModel;

@RequiredArgsConstructor
public class Organisasjon {
    private final MNOrganisasjonDTO dto;

    public Organisasjon(OrganisasjonDTO dto, boolean active) {
        this.dto = new MNOrganisasjonDTO(dto, active);
    }

    public String getOrgnummer() {
        return dto.getOrgnummer();
    }

    public MNOrganisasjonDTO toDTO() {
        return dto;
    }

    public OrganisasjonModel toModel(Long id, String miljo) {
        return OrganisasjonModel
                .builder()
                .id(id)
                .active(dto.getActive())
                .environment(miljo)
                .orgnummer(dto.getOrgnummer())
                .build();
    }

}
