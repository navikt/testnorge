package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums;

public enum AarsakBeregnetInntektEndringKoder implements AltinnEnum {
    TARIFFENDRING("Tariffendring"),
    FEIL_INNTEKT("FeilInntekt");

    private String value;

    AarsakBeregnetInntektEndringKoder(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
