package no.nav.registre.testnorge.arbeidsforholdservice.consumer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;
import no.nav.registre.testnorge.arbeidsforholdservice.domain.v2.ArbeidsforholdResponse.Periode;

@Value
@Builder
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class AntallTimerForTimeloennetDTO {
    Double antallTimer;
    Periode periode;
    String rapporteringsperiode;
}
