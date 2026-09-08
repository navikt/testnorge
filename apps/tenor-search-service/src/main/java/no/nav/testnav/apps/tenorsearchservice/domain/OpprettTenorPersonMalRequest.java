package no.nav.testnav.apps.tenorsearchservice.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.JsonNode;

public record OpprettTenorPersonMalRequest(
        @NotBlank @Size(max = 100) String malNavn,
        @NotNull
        @Schema(
                implementation = TenorRequest.class,
                description = "Søkekriterier fra Tenor-søket. Fødselsnummer og d-nummer kan ikke lagres i maler.")
        JsonNode soekKriterier
) {
}
