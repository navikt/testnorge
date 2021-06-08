package no.nav.registre.testnorge.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Size;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class RsArbeidsgiver {

    @JsonProperty
    @Size(min = 9, max = 9)
    private String virksomhetsnummer;
    @JsonProperty
    private RsKontaktinformasjon kontaktinformasjon;

}
