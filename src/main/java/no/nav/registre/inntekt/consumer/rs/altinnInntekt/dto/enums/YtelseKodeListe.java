package no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.enums;

import com.fasterxml.jackson.annotation.JsonValue;

public enum YtelseKodeListe implements AltinnEnum {
    SYKEPENGER("Sykepenger"),
    FORELDREPENGER("Foreldrepenger"),
    SVANGERSKAPSPENGER("Svangerskapspenger"),
    PLEIEPENGER("Pleiepenger"),
    OMSORGSPENGER("Omsorgspenger"),
    OPPLAERINGSPENGER("Opplaeringspenger");

    private String value;

    YtelseKodeListe (String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return value;
    }
}
