package no.nav.testnav.libs.dto.inntektsmeldinggeneratorservice.v1.rs;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class RsArbeidsgiverPrivat {

    @JsonProperty
    @Size(min = 11, max = 11)
    private String arbeidsgiverFnr;
    @JsonProperty
    private RsKontaktinformasjon kontaktinformasjon;

}
