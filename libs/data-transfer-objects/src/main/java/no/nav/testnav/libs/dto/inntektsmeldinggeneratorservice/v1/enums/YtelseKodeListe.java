package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.enums;


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
