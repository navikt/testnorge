package no.nav.testnorge.apps.levendearbeidsforholdansettelse.domain.v1;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Entitet for varsel")
public enum Varselentitet {

    ARBEIDSFORHOLD,
    ANSETTELSESPERIODE,
    PERMISJONPERMITTERING
}
