package no.nav.dolly.bestilling.skattekort.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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

    private final String description;
}
