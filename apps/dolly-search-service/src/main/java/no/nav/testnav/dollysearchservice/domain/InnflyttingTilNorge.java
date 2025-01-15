package no.nav.testnav.dollysearchservice.domain;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.dollysearchservice.model.InnflyttingTilNorgeModel;
import no.nav.testnav.libs.dto.personsearchservice.v1.InnflyttingTilNorgeDTO;

@RequiredArgsConstructor
public class InnflyttingTilNorge implements WithDTO<InnflyttingTilNorgeDTO> {
   private final InnflyttingTilNorgeModel model;

    @Override
    public InnflyttingTilNorgeDTO toDTO() {
        return InnflyttingTilNorgeDTO
                .builder()
                .fraflyttingsland(model.getFraflyttingsland())
                .fraflyttingsstedIUtlandet(model.getFraflyttingsstedIUtlandet())
                .build();
    }
}
