package no.nav.testnav.apps.tenorsearchservice.domain;

import tools.jackson.databind.JsonNode;

import java.time.Instant;

public record TenorMalResponse(
        Long id,
        String malNavn,
        TenorMalType malType,
        JsonNode soekKriterier,
        String brukerId,
        String brukernavn,
        Instant opprettet,
        Instant sistOppdatert
) {
}
