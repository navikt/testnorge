package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.util.List;
import no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.enums.BegrunnelseIngenEllerRedusertUtbetalingKodeListe;

@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsSykepengerIArbeidsgiverperioden {

    @JsonProperty
    private List<RsPeriode> arbeidsgiverperiodeListe;

    @JsonProperty
    private Double bruttoUtbetalt;

    @JsonProperty
    private BegrunnelseIngenEllerRedusertUtbetalingKodeListe begrunnelseForReduksjonEllerIkkeUtbetalt;

}
