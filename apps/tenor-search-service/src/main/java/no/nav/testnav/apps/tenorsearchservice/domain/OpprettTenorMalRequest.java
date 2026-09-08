package no.nav.testnav.apps.tenorsearchservice.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import tools.jackson.databind.JsonNode;

public record OpprettTenorMalRequest(
        @NotBlank @Size(max = 100) String malNavn,
        @NotNull TenorMalType malType,
        @NotNull JsonNode soekKriterier
) {
}
