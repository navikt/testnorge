package no.nav.testnav.identpool.dto;

import jakarta.validation.constraints.NotBlank;

public record ValideringFlereRequestDTO(

        @NotBlank
        String identer) {
}