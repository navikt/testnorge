package no.nav.testnav.apps.oversiktfrontend.consumer.dto;

import java.time.LocalDateTime;

public record BrukerDTO(
        String id,
        String brukernavn,
        String organisasjonsnummer,
        LocalDateTime opprettet,
        LocalDateTime sistInnlogget) {
}
