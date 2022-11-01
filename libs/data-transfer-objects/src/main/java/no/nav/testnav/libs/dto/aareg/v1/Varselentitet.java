package no.nav.testnav.libs.dto.aareg.v1;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Entitet for varsel")
public enum Varselentitet {

    ARBEIDSFORHOLD,
    ANSETTELSESPERIODE,
    PERMISJONPERMITTERING
}
