package no.nav.registre.inntekt.domain.altinn.enums;

public enum AarsakBeregnetInntektEndringKodeListe {
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
