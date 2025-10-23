package no.nav.testnav.identpool.dto;

import java.time.LocalDate;

public record IdentpoolResponseDTO(String personidentifikator, LocalDate foedselsdato, Long millis) {
}
