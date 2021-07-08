package no.nav.registre.testnorge.arbeidsforholdservice.domain;

import lombok.AllArgsConstructor;
import no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto.AntallTimerForTimeloennetDTO;

@AllArgsConstructor
public class AntallTimerForTimeloennet {
    private final AntallTimerForTimeloennetDTO dto;

    public no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.AntallTimerForTimeloennetDTO toDTO(){
        return no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.AntallTimerForTimeloennetDTO
                .builder()
                .build();
    }
}
