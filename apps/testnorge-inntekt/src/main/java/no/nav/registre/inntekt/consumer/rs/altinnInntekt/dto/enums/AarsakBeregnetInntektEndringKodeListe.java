package no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.enums;

public enum AarsakBeregnetInntektEndringKodeListe implements AltinnEnum {
    TARIFFENDRING("Tariffendring"),
    FEIL_INNTEKT("FeilInntekt");

    private String value;

    AarsakBeregnetInntektEndringKodeListe (String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
