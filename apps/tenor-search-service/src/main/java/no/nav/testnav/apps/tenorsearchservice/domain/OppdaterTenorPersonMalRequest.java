package no.nav.testnav.apps.tenorsearchservice.domain;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record OppdaterTenorPersonMalRequest(
        @NotBlank @Size(max = 100) String malNavn
) {
}
