package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class RsSykepengerIArbeidsgiverperioden {

    private List<RsPeriode> arbeidsgiverperiodeListe;
    private Double bruttoUtbetalt;
    private String begrunnelseForReduksjonEllerIkkeUtbetalt;
}
