package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.enums;

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
