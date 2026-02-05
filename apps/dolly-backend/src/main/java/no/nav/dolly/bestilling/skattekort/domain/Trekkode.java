package no.nav.dolly.bestilling.skattekort.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Getter
@RequiredArgsConstructor
public enum Trekkode {

    LOENN_FRA_HOVEDARBEIDSGIVER("loennFraHovedarbeidsgiver"),
    LOENN_FRA_BIARBEIDSGIVER("loennFraBiarbeidsgiver"),
    LOENN_FRA_NAV("loennFraNAV"),
    PENSJON("pensjon"),
    PENSJON_FRA_NAV("pensjonFraNAV"),
    LOENN_TIL_UTENRIKSTJENESTEMANN("loennTilUtenrikstjenestemann"),
    LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER("loennKunTrygdeavgiftTilUtenlandskBorger"),
    LOENN_KUN_TRYGDEAVGIFT_TIL_UTENLANDSK_BORGER_SOM_GRENSEGJENGER("loennKunTrygdeavgiftTilUtenlandskBorgerSomGrensegjenger"),
    UFOERETRYGD_FRA_NAV("ufoeretrygdFraNAV"),
    UFOEREYTELSER_FRA_ANDRE("ufoereytelserFraAndre"),
    INTRODUKSJONSSTOENAD("introduksjonsstoenad");

    @JsonValue
    private final String value;

    @JsonCreator
    public static Trekkode fromValue(String value) {
        if (isNull(value)) {
            return null;
        }
        return Stream.of(values())
                .filter(kode -> kode.value.equalsIgnoreCase(value) || kode.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Trekkode: " + value));
    }

    @Override
    public String toString() {
        return this.name() + "," + value;
    }
}
