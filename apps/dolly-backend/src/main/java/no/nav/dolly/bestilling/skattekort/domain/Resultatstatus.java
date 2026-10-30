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
    UTGAATT_DNUMMER_SKATTEKORT_FOR_FOEDSELSNUMMER_ER_LEVERT("utgaattDnummerSkattekortForFoedselsnummerErLevert"),
    REGISTRERT_SOM_DOED("registrertSomDoed"),
    TREKK_ETTER_REGLER_OM_KILDESKATT_PAA_PENSJON_15_PROSENT("trekkEtterReglerOmKildeskattPaaPensjon15Prosent");

    private final String description;
}
