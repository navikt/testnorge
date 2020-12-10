package no.nav.registre.sdforvalter.domain;

import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

import no.nav.registre.sdforvalter.consumer.rs.dto.KodeDTO;

@RequiredArgsConstructor
@EqualsAndHashCode
public class Kode {
    private final KodeDTO dto;

    public String getNavn() {
        return dto.getNavn();
    }

    public LocalDate getGyldigFra() {
        return dto.getGyldigFra();
    }
}
