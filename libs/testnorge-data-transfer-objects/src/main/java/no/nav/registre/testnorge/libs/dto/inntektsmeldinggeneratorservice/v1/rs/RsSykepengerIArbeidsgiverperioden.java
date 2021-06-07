package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty
    private List<RsPeriode> arbeidsgiverperiodeListe;
    @JsonProperty
    private Double bruttoUtbetalt;
    @JsonProperty
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
