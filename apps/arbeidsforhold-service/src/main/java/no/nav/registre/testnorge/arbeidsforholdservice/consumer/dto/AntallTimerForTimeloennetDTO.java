package no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.testnorge.arbeidsforholdservice.provider.v2.dto.PeriodeDTO;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AntallTimerForTimeloennetDTO {
    Double antallTimer;
    PeriodeDTO periode;
    String rapporteringsperiode;
}
