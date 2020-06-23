package no.nav.registre.sdforvalter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import no.nav.registre.sdforvalter.database.model.FasteDataModel;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
public abstract class FasteData {
    @JsonProperty
    private final String gruppe;
    @JsonProperty
    private final String opprinnelse;

    public FasteData(FasteDataModel<? extends FasteData> model) {
        gruppe = model.getGruppeModel() != null ? model.getGruppeModel().getKode() : null;
        opprinnelse = model.getOpprinnelseModel() != null ? model.getOpprinnelseModel().getNavn() : null;
    }
}