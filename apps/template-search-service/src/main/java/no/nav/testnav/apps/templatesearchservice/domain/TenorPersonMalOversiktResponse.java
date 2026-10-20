package no.nav.testnav.apps.templatesearchservice.domain;

import java.util.List;

public record TenorPersonMalOversiktResponse(
        List<TenorPersonMalBrukerResponse> brukereMedMaler
) {
}
