package no.nav.registre.testnorge.profil.consumer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrganisasjonDTO(
        String navn,
        String organisasjonsnummer,
        String organisasjonsfrom,
        LocalDateTime gyldigTil
) {
}
