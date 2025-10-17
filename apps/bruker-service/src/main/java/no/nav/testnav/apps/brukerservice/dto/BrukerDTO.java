package no.nav.testnav.apps.brukerservice.dto;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record BrukerDTO(
        String id,
        String brukernavn,
        String epost,
        String organisasjonsnummer,
        LocalDateTime opprettet,
        LocalDateTime sistInnlogget
) {

}
