package no.nav.testnav.libs.dto.skattekortservice.v1;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tilleggsopplysning {

    OPPHOLD_PAA_SVALBARD("oppholdPaaSvalbard"),
    KILDESKATTPENSJONIST("kildeskattpensjonist"),
    OPPHOLD_I_TILTAKSSONE("oppholdITiltakssone"),
    KILDESKATT_PAA_LOENN("kildeskattPaaLoenn");

    @JsonValue
    private final String value;

    @Override
    public String toString() {
        return this.name() + "," + value;
    }
}