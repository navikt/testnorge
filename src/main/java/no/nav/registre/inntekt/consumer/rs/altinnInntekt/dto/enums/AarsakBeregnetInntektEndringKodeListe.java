package no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AarsakBeregnetInntektEndringKodeListe implements AltinnEnum {
    TARIFFENDRING("Tariffendring"),
    FEIL_INNTEKT("FeilInntekt");

    private String value;

    AarsakBeregnetInntektEndringKodeListe (String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
