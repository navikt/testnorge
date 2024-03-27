package no.nav.testnav.libs.dto.skattekortservice.v1;

public enum Tilleggsopplysning {

    OPPHOLD_PAA_SVALBARD("oppholdPaaSvalbard"),
    KILDESKATTPENSJONIST("kildeskattpensjonist"),
    OPPHOLD_I_TILTAKSSONE("oppholdITiltakssone"),
    KILDESKATT_PAA_LOENN("kildeskattPaaLoenn");
    private final String value;

    Tilleggsopplysning(String v) {
        value = v;
    }

    public String value() {
        return value;
    }
}