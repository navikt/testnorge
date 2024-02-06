package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RsArbeidsgiver {

    @JsonProperty
    @Size(min = 9, max = 9)
    private String virksomhetsnummer;
    @JsonProperty
    private RsKontaktinformasjon kontaktinformasjon;

}
