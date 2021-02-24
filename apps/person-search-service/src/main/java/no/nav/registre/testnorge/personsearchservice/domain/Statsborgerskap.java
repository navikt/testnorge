package no.nav.registre.testnorge.personsearchservice.domain;

import lombok.RequiredArgsConstructor;

import no.nav.registre.testnorge.personsearchservice.controller.dto.StatsborgerskapDTO;

@RequiredArgsConstructor
public class Statsborgerskap {
    private final no.nav.registre.testnorge.personsearchservice.adapter.model.Statsborgerskap statsborgerskap;

    public String getLand() {
        return statsborgerskap != null ? statsborgerskap.getLand() : null;
    }

    public StatsborgerskapDTO toDTO() {
        return StatsborgerskapDTO
                .builder()
                .land(getLand())
                .build();
    }

}