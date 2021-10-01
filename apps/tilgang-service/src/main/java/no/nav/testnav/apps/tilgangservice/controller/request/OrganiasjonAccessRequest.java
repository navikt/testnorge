package no.nav.testnav.apps.tilgangservice.controller.request;

import java.time.LocalDateTime;

public record OrganiasjonAccessRequest(
        String organisajonsnummer,
        LocalDateTime gyldigTil
) {
}
