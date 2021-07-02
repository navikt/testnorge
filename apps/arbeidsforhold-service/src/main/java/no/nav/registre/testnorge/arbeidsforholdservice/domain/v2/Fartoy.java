package no.nav.registre.testnorge.arbeidsforholdservice.domain.v2;

import lombok.AllArgsConstructor;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.ArbeidsavtaleDTO;
import no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.FartoyDTO;

@AllArgsConstructor
public class Fartoy {

    private final ArbeidsavtaleDTO dto;

    public FartoyDTO toDTO() {
        return FartoyDTO.builder()
                .fartsomraade(dto.getFartsomraade())
                .skipsregister(dto.getSkipsregister())
                .skipstype(dto.getSkipstype())
                .build();
    }

}
