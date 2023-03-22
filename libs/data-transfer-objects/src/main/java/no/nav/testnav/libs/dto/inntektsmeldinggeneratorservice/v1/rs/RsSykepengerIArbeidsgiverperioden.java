package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsSykepengerIArbeidsgiverperioden {

    private List<RsPeriode> arbeidsgiverperiodeListe;
    private Double bruttoUtbetalt;
    private String begrunnelseForReduksjonEllerIkkeUtbetalt;

    public Optional<List<RsPeriode>> getArbeidsgiverperiodeListe() {
        return Optional.ofNullable(arbeidsgiverperiodeListe);
    }

    public Optional<Double> getBruttoUtbetalt() {
        return Optional.ofNullable(bruttoUtbetalt);
    }

    public Optional<String> getBegrunnelseForReduksjonEllerIkkeUtbetalt() {
        return Optional.ofNullable(begrunnelseForReduksjonEllerIkkeUtbetalt);
    }
}