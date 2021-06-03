package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Size;

@Value
@NoArgsConstructor(force = true)
@AllArgsConstructor
@Builder
public class RsArbeidsgiverPrivat {

    @JsonProperty
    @Size(min = 11, max = 11)
    private String arbeidsgiverFnr;

    @JsonProperty
    private RsKontaktinformasjon kontaktinformasjon;
}
