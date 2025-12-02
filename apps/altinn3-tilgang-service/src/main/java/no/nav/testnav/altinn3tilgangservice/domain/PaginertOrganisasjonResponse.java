package no.nav.testnav.altinn3tilgangservice.domain;

import java.util.List;

public record PaginertOrganisasjonResponse (
        int page,
        int size,
        long totalElements,
        int totaltPages,
        List<OrganisasjonResponse> organisasjoner
) {
}
