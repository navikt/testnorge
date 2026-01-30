package no.nav.testnav.libs.dto.skattekortservice.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
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

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    Trekkode(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.name() + "," + value;
    }
}

