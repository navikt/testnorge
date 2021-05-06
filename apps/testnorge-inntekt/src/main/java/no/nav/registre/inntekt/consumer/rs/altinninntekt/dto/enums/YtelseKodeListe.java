package no.nav.registre.inntekt.consumer.rs.altinninntekt.dto.enums;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import no.nav.registre.inntekt.utils.JsonYtelseKodeListeDeserializer;

@JsonDeserialize(using = JsonYtelseKodeListeDeserializer.class)
public enum YtelseKodeListe implements AltinnEnum {
    SYKEPENGER("Sykepenger"),
    FORELDREPENGER("Foreldrepenger"),
    SVANGERSKAPSPENGER("Svangerskapspenger"),
    PLEIEPENGER("Pleiepenger"),
    PLEIEPENGER_BARN("PleiepengerBarn"),
    PLEIEPENGER_NAERSTAAENDE("PleiepengerNaerstaaende"),
    OMSORGSPENGER("Omsorgspenger"),
    OPPLAERINGSPENGER("Opplaeringspenger");

    private String value;

    YtelseKodeListe(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
