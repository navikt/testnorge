package no.nav.testnav.apps.tilgangservice.controller.request;

import java.time.LocalDateTime;

public record OrganisasjonAccessRequest(
        String organisajonsnummer,
        LocalDateTime gyldigTil
) {
}
