package no.nav.testnav.libs.dto.skattekortservice.v1;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
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

    @JsonCreator(mode = JsonCreator.Mode.DELEGATING)
    Resultatstatus(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return this.name() + "," + value;
    }
}