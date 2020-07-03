package no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AarsakUtsettelseKodeListe implements AltinnEnum {
    ARBEID("Arbeid"),
    LOVBESTEMT_FERIE("LovbestemtFerie");

    private String value;

    AarsakUtsettelseKodeListe (String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
