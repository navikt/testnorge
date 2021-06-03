package no.nav.registre.testnav.inntektsmeldingservice.consumer.dto.inntektsmeldinggenerator.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.Size;

@Value
@NoArgsConstructor(force = true)
@Builder
@AllArgsConstructor
public class RsKontaktinformasjon {

    @JsonProperty
    private String kontaktinformasjonNavn;

    @JsonProperty
    @Size(min = 8, max = 8)
    private String telefonnummer;
}
