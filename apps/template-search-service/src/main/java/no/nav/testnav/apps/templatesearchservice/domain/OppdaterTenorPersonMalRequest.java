package no.nav.testnav.apps.templatesearchservice.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OppdaterTenorPersonMalRequest(
        @NotBlank @Size(max = 100) String malNavn
) {
}
