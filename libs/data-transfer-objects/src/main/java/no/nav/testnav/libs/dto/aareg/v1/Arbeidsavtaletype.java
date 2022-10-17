package no.nav.testnav.libs.dto.aareg.v1;

import io.swagger.v3.oas.annotations.media.Schema;

public interface Arbeidsavtaletype {

    @Schema(description = "Type for arbeidsavtale", allowableValues = "Ordinaer,Maritim,Forenklet,Frilanser")
    String getType();
}
