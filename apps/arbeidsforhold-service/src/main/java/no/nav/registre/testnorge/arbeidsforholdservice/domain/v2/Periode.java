package no.nav.registre.testnorge.arbeidsforholdservice.domain.v2;

import lombok.AllArgsConstructor;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.PeriodeDTO;

@AllArgsConstructor
public class Periode {

    private final PeriodeDTO dto;

    public no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.PeriodeDTO toDTO() {

        return no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.PeriodeDTO.builder()
                .fom(dto.getFom())
                .tom(dto.getTom())
                .build();
    }

}
