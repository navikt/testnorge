package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Data
@NoArgsConstructor
public class RsGraderingIForeldrepenger {

    @JsonProperty
    private RsPeriode periode;
    @JsonProperty
    private Integer arbeidstidprosent;

    public Optional<RsPeriode> getPeriode() {
        return Optional.ofNullable(periode);
    }

    public Optional<Integer> getArbeidstidprosent() {
        return Optional.ofNullable(arbeidstidprosent);
    }
}
