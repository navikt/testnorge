package no.nav.testnav.apps.tilgangservice.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrganisajonDTO(
        String navn,
        String orgnisajonsnummer,
        String orgnisajonsfrom,
        LocalDateTime gyldigTil
) {
}
