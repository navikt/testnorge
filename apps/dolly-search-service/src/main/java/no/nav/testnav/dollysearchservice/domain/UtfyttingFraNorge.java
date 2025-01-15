package no.nav.testnav.dollysearchservice.domain;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.dollysearchservice.model.UtflyttingFraNorgeModel;
import no.nav.testnav.libs.dto.personsearchservice.v1.UtfyttingFraNorgeDTO;

@RequiredArgsConstructor
public class UtfyttingFraNorge implements WithDTO<UtfyttingFraNorgeDTO> {
    private final UtflyttingFraNorgeModel utflyttingFraNorge;

    public UtfyttingFraNorgeDTO toDTO() {
        return UtfyttingFraNorgeDTO
                .builder()
                .tilflyttingsland(utflyttingFraNorge.getTilflyttingsland())
                .utflyttingsdato(utflyttingFraNorge.getUtflyttingsdato())
                .tilflyttingsstedIUtlandet(utflyttingFraNorge.getTilflyttingsstedIUtlandet())
                .build();
    }
}
