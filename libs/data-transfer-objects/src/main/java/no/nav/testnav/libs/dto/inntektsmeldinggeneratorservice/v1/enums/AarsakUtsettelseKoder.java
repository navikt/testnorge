package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums;

public enum AarsakUtsettelseKoder implements AltinnEnum {
    ARBEID("Arbeid"),
    LOVBESTEMT_FERIE("LovbestemtFerie");

    private String value;

    AarsakUtsettelseKoder(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
