package no.nav.dolly.bestilling.skattekort.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum Tilleggsopplysning {

    OPPHOLD_PAA_SVALBARD("oppholdPaaSvalbard"),
    KILDESKATTPENSJONIST("kildeskattpensjonist"),
    OPPHOLD_I_TILTAKSSONE("oppholdITiltakssone"),
    KILDESKATT_PAA_LOENN("kildeskattPaaLoenn");

    @JsonValue
    private final String value;

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    Tilleggsopplysning(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.name() + "," + value;
    }
}
