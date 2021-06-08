package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Optional;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
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
