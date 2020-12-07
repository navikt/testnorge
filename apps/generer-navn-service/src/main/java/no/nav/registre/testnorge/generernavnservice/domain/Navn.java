package no.nav.registre.testnorge.generernavnservice.domain;

import lombok.AllArgsConstructor;

import no.nav.registre.testnorge.libs.dto.generernavnservice.v1.NavnDTO;

@AllArgsConstructor
public class Navn {
    private final String adjektiv;
    private final String substantiv;

    public NavnDTO toDTO() {
        return NavnDTO
                .builder()
                .adjektiv(adjektiv)
                .substantiv(substantiv)
                .build();
    }
}
