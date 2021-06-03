package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import java.time.LocalDate;

@Builder
@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
public class RsPeriode {

    @JsonProperty
    private LocalDate fom;
    @JsonProperty
    private LocalDate tom;

}