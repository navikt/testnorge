package no.nav.testnav.apps.tenorsearchservice.domain;

import java.util.List;

public record TenorPersonMalOversiktResponse(
        List<TenorPersonMalBrukerResponse> brukereMedMaler
) {
}
