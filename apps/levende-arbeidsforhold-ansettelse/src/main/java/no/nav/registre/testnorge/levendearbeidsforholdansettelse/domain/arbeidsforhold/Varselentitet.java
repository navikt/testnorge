package no.nav.registre.testnorge.levendearbeidsforholdansettelse.domain.arbeidsforhold;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Entitet for varsel")
public enum Varselentitet {

    ARBEIDSFORHOLD,
    ANSETTELSESPERIODE,
    PERMISJONPERMITTERING
}
