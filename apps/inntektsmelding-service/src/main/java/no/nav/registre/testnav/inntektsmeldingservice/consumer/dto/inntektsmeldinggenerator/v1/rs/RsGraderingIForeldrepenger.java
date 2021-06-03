package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsGraderingIForeldrepenger {

    @JsonProperty
    private RsPeriode periode;

    @JsonProperty
    private Integer arbeidstidprosent;

}
