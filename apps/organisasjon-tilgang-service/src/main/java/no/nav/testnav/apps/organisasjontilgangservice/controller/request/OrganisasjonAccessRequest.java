package no.nav.testnav.apps.organisasjontilgangservice.controller.request;

import java.time.LocalDateTime;

public record OrganisasjonAccessRequest(
        String organisasjonsnummer,
        LocalDateTime gyldigTil
) {
}
