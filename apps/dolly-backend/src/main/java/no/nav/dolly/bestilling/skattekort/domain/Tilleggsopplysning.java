package no.nav.dolly.bestilling.skattekort.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Tilleggsopplysning {

    OPPHOLD_PAA_SVALBARD("oppholdPaaSvalbard"),
    KILDESKATT_PAA_PENSJON("kildeskattPaaPensjon"),
    OPPHOLD_I_TILTAKSSONE("oppholdITiltakssone"),
    KILDESKATT_PAA_LOENN("kildeskattPaaLoenn");

    private final String description;
}
