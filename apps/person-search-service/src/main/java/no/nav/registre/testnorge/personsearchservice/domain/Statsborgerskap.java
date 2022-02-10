package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.personsearchservice.adapter.model.StatsborgerskapModel;
import no.nav.testnav.libs.dto.personsearchservice.v1.StatsborgerskapDTO;

@RequiredArgsConstructor
public class Statsborgerskap implements WithDTO<StatsborgerskapDTO> {
    private final StatsborgerskapModel statsborgerskap;

    public StatsborgerskapDTO toDTO() {
        return StatsborgerskapDTO
                .builder()
                .land(statsborgerskap.getLand())
                .build();
    }

}