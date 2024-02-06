package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RsSykepengerIArbeidsgiverperioden {

    @JsonProperty
    private List<RsPeriode> arbeidsgiverperiodeListe;
    @JsonProperty
    private Double bruttoUtbetalt;
    @JsonProperty
    private String begrunnelseForReduksjonEllerIkkeUtbetalt;
}
