package no.nav.registre.inntekt.domain.altinn.enums;

public enum AarsakInnsendingKodeListe implements AltinnEnum {
    NY("Ny"),
    ENDRING("Endring");

    private String value;

    AarsakInnsendingKodeListe (String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
