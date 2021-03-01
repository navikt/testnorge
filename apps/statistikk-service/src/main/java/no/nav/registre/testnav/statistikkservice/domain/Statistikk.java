package no.nav.registre.testnav.statistikkservice.domain;

import lombok.Value;

import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkDTO;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkType;
import no.nav.registre.testnorge.libs.dto.statistikkservice.v1.StatistikkValueType;
import no.nav.registre.testnav.statistikkservice.repository.model.StatistikkModel;

@Value
public class Statistikk {
    StatistikkType type;
    String description;
    Double value;
    StatistikkValueType valueType;

    public Statistikk(StatistikkModel model) {
        type = model.getType();
        description = model.getDescription();
        value = model.getValue();
        valueType  = model.getValueType();
    }

    public Statistikk(StatistikkDTO dto) {
        type = dto.getType();
        description = dto.getDescription();
        value = dto.getValue();
        valueType = dto.getValueType();
    }

    public StatistikkModel toModel() {
        return StatistikkModel
                .builder()
                .type(type)
                .description(description)
                .value(value)
                .valueType(valueType)
                .build();
    }

    public StatistikkDTO toDTO() {
        return StatistikkDTO
                .builder()
                .type(type)
                .description(description)
                .valueType(valueType)
                .value(value)
                .build();
    }
}
