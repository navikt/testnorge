package no.nav.testnav.apps.templatesearchservice.consumers.dto;

import lombok.Builder;

import java.util.List;

import static java.util.Objects.isNull;

@Builder
public record BrukereDTO(List<String> brukere) {
    public BrukereDTO {
        if (isNull(brukere)) {
            brukere = List.of();
        }
    }
}
