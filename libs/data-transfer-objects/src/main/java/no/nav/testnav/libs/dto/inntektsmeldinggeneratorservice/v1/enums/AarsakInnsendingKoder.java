package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums;

public enum AarsakInnsendingKoder implements AltinnEnum {
    NY("Ny"),
    ENDRING("Endring");

    private String value;

    AarsakInnsendingKoder(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
