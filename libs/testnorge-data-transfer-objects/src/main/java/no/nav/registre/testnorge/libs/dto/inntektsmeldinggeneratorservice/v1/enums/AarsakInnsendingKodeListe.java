package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.enums;

public enum AarsakInnsendingKodeListe implements AltinnEnum {
    NY("Ny"),
    ENDRING("Endring");

    private String value;

    AarsakInnsendingKodeListe (String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
