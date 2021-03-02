package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.personsearchservice.controller.dto.UtfyttingFraNorgeDTO;

@RequiredArgsConstructor
public class UtfyttingFraNorge {
    private final no.nav.registre.testnorge.personsearchservice.adapter.model.UtflyttingFraNorge utflyttingFraNorge;


    public UtfyttingFraNorgeDTO toDTO() {
        return UtfyttingFraNorgeDTO
                .builder()
                .tilflyttingsland(utflyttingFraNorge.getTilflyttingsland())
                .build();
    }
}
