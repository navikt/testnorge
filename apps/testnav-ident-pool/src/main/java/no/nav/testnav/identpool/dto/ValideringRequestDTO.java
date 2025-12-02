package no.nav.testnav.identpool.dto;

import jakarta.validation.constraints.NotBlank;

public record ValideringRequestDTO(

        @NotBlank
        String ident) {
}