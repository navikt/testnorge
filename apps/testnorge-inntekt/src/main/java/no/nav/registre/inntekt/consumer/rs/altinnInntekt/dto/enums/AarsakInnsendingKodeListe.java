package no.nav.registre.inntekt.consumer.rs.altinnInntekt.dto.enums;

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
