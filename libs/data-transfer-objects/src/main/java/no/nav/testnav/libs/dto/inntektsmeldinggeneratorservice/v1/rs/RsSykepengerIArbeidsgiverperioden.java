package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Optional;

@Data
@NoArgsConstructor
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
