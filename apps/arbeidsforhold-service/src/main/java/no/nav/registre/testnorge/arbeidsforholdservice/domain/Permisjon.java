package no.nav.registre.testnorge.arbeidsforholdservice.domain;

import lombok.AllArgsConstructor;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.PermisjonPermitteringDTO;

@AllArgsConstructor
public class Permisjon {

    private final PermisjonPermitteringDTO dto;

    public no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.PermisjonPermitteringDTO toDTO() {


        return no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.PermisjonPermitteringDTO.builder()
                .periode(dto.getPeriode() == null ? null : new Periode(dto.getPeriode()).toDTO())
                .type(dto.getType())
                .prosent(dto.getProsent() == null ? null : dto.getProsent().doubleValue())
                .permisjonPermitteringId(dto.getPermisjonPermitteringId())
                .build();
    }

}
