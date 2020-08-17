package no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum AarsakInnsendingKodeListe implements AltinnEnum {
    NY("Ny"),
    ENDRING("Endring");

    private String value;

    AarsakInnsendingKodeListe (String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
