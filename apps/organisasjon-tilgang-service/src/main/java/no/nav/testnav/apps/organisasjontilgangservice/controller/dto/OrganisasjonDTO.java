package no.nav.testnav.apps.organisasjontilgangservice.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrganisasjonDTO(
        String navn,
        String orgnisasjonsnummer,
        String orgnisasjonsfrom,
        LocalDateTime gyldigTil
) {
}
