package no.nav.registre.sdForvalter.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Value;

import no.nav.registre.sdForvalter.database.model.GruppeModel;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
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
