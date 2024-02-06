package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RsGraderingIForeldrepenger {

    @JsonProperty
    private RsPeriode periode;
    @JsonProperty
    private Integer arbeidstidprosent;
}
