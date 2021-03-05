package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.personsearchservice.controller.dto.InnflyttingTilNorgeDTO;

@RequiredArgsConstructor
public class InnflyttingTilNorge implements WithDTO<InnflyttingTilNorgeDTO> {
   private final no.nav.registre.testnorge.personsearchservice.adapter.model.InnflyttingTilNorge model;

    @Override
    public InnflyttingTilNorgeDTO toDTO() {
        return InnflyttingTilNorgeDTO
                .builder()
                .fraflyttingsland(model.getFraflyttingsland())
                .fraflyttingsstedIUtlandet(model.getFraflyttingsstedIUtlandet())
                .fraflyttingsstedIUtlandet(model.getFraflyttingsstedIUtlandet())
                .build();
    }
}
