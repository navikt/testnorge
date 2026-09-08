package no.nav.testnav.apps.tenorsearchservice.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import tools.jackson.databind.JsonNode;

import java.time.Instant;

public record TenorPersonMalResponse(
        Long id,
        String malNavn,
        @Schema(implementation = TenorRequest.class)
        JsonNode soekKriterier,
        Instant opprettet,
        Instant sistOppdatert
) {
}
