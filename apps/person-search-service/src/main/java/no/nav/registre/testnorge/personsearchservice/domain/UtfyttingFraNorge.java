package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.personsearchservice.adapter.model.UtflyttingFraNorgeModel;
import no.nav.registre.testnorge.personsearchservice.controller.dto.UtfyttingFraNorgeDTO;

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
