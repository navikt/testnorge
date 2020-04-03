package no.nav.registre.inntekt.domain.altinn.enums;

public enum AarsakUtsettelseKodeListe implements AltinnEnum {
    ARBEID("Arbeid"),
    LOVBESTEMT_FERIE("LovbestemtFerie");

    private String value;

    AarsakUtsettelseKodeListe (String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
