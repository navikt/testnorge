package no.nav.testnav.altinn3tilgangservice.controller.request;

import java.time.LocalDateTime;

public record OrganisasjonAccessRequest(

        String organisasjonsnummer,
        LocalDateTime gyldigTil,
        String miljoe
) {
}
