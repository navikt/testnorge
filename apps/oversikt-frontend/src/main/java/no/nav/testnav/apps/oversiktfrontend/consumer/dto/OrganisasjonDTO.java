package no.nav.testnav.apps.oversiktfrontend.consumer.dto;

import java.time.LocalDateTime;

public record OrganisasjonDTO(
        String navn,
        String orgnisasjonsnummer,
        String orgnisasjonsfrom,
        LocalDateTime gyldigTil
) {
}
