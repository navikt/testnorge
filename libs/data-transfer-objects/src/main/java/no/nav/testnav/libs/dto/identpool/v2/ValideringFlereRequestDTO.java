package no.nav.testnav.libs.dto.identpool.v2;

import jakarta.validation.constraints.NotBlank;

public record ValideringFlereRequestDTO(

        @NotBlank
        String identer) {
}