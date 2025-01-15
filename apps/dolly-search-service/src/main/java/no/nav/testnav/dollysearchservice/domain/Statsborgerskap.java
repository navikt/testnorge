package no.nav.testnav.dollysearchservice.domain;

import lombok.RequiredArgsConstructor;
import no.nav.testnav.dollysearchservice.model.StatsborgerskapModel;
import no.nav.testnav.libs.dto.personsearchservice.v1.StatsborgerskapDTO;

import java.util.List;

@RequiredArgsConstructor
public class Statsborgerskap implements WithDTO<StatsborgerskapDTO> {
    private final List<StatsborgerskapModel> statsborgerskap;

    public StatsborgerskapDTO toDTO() {
        var land = statsborgerskap.stream().map(StatsborgerskapModel::getLand).toList();
        return StatsborgerskapDTO
                .builder()
                .land(land)
                .build();
    }

}