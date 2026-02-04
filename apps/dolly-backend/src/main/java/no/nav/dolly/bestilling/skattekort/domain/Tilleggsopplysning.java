package no.nav.dolly.bestilling.skattekort.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Getter
@RequiredArgsConstructor
public enum Tilleggsopplysning {

    OPPHOLD_PAA_SVALBARD("oppholdPaaSvalbard"),
    KILDESKATT_PAA_PENSJON("kildeskattPaaPensjon"),
    OPPHOLD_I_TILTAKSSONE("oppholdITiltakssone"),
    KILDESKATT_PAA_LOENN("kildeskattPaaLoenn");

    @JsonValue
    private final String value;

    @JsonCreator
    public static Tilleggsopplysning fromValue(String value) {
        if (isNull(value)) {
            return null;
        }
        return Stream.of(values())
                .filter(opplysning -> opplysning.value.equalsIgnoreCase(value) || opplysning.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Tilleggsopplysning: " + value));
    }

    @Override
    public String toString() {
        return this.name() + "," + value;
    }
}
