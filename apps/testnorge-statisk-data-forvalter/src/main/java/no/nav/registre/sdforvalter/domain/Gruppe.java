package no.nav.registre.sdforvalter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import no.nav.registre.sdforvalter.database.model.GruppeModel;

@Getter
@Schema(description = "En definert gruppe.")
public class Gruppe {

    @JsonProperty
    private final String kode;
    @JsonProperty
    private final String beskrivelse;

    public Gruppe(GruppeModel model) {
        kode = model.getKode();
        beskrivelse = model.getBeskrivelse();
    }

}
