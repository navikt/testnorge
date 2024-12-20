package no.nav.dolly.web.consumers.dto;

import java.time.LocalDateTime;

public record OrganisasjonDTO(
        String navn,
        String orgnisasjonsnummer,
        String orgnisasjonsfrom,
        LocalDateTime gyldigTil
) {
}

