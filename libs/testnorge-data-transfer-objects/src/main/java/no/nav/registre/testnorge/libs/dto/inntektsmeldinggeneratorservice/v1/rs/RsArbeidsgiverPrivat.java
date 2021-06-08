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
public class RsArbeidsgiverPrivat {

    @JsonProperty
    @Size(min = 11, max = 11)
    private String arbeidsgiverFnr;
    @JsonProperty
    private RsKontaktinformasjon kontaktinformasjon;

}
