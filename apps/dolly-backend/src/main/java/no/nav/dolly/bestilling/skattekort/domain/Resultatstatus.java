package no.nav.dolly.bestilling.skattekort.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.stream.Stream;

import static java.util.Objects.isNull;

@Getter
@RequiredArgsConstructor
public enum Resultatstatus {

    IKKE_SKATTEKORT("ikkeSkattekort"),
    VURDER_ARBEIDSTILLATELSE("vurderArbeidstillatelse"),
    IKKE_TREKKPLIKT("ikkeTrekkplikt"),
    SKATTEKORTOPPLYSNINGER_OK("skattekortopplysningerOK"),
    UGYLDIG_ORGANISASJONSNUMMER("ugyldigOrganisasjonsnummer"),
    UGYLDIG_FOEDSELS_ELLER_DNUMMER("ugyldigFoedselsEllerDnummer"),
    UTGAATT_DNUMMER_SKATTEKORT_FOR_FOEDSELSNUMMER_ER_LEVERT("utgaattDnummerSkattekortForFoedselsnummerErLevert");

    @JsonValue
    private final String value;

    @JsonCreator
    public static Resultatstatus fromValue(String value) {
        if (isNull(value)) {
            return null;
        }
        return Stream.of(values())
                .filter(status -> status.value.equalsIgnoreCase(value) || status.name().equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown Resultatstatus: " + value));
    }

    @Override
    public String toString() {
        return this.name() + "," + value;
    }

}
