package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import no.nav.registre.sdForvalter.database.model.FasteDataModel;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public abstract class FasteData {
    @JsonProperty
    private final String gruppe;
    @JsonProperty
    private final String opprinelse;

    FasteData(FasteDataModel model) {
        gruppe = model.getGruppeModel() != null ? model.getGruppeModel().getKode() : null;
        opprinelse = model.getOpprinnelseModel() != null ? model.getOpprinnelseModel().getNavn() : null;
    }
}